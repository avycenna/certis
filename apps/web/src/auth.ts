import NextAuth from "next-auth"
import Credentials from "next-auth/providers/credentials"
import type { JWT } from "next-auth/jwt"

/**
 * Refreshes the access token by calling the Spring Boot /auth/refresh endpoint
 */
async function refreshAccessToken(token: JWT): Promise<JWT> {
  try {
    const apiUrl = process.env.NEXT_PUBLIC_API_URL!
    
    const response = await fetch(`${apiUrl}/auth/refresh`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(token.accessToken),
    })

    if (!response.ok) {
      throw new Error("Failed to refresh token")
    }

    const data = await response.json()

    return {
      ...token,
      accessToken: data.token,
      accessTokenExpires: Date.now() + 86400000, // 24 hours
    }
  } catch {
    return {
      ...token,
      error: "RefreshAccessTokenError",
    }
  }
}

export const { handlers, signIn, signOut, auth } = NextAuth({
  providers: [
    Credentials({
      name: "Credentials",
      credentials: {
        email: { label: "Email", type: "email" },
        password: { label: "Password", type: "password" }
      },
      async authorize(credentials) {
        if (!credentials?.email || !credentials?.password) {
          return null
        }

        try {
          const apiUrl = process.env.NEXT_PUBLIC_API_URL!
          
          const response = await fetch(`${apiUrl}/auth/login`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({
              email: credentials.email,
              password: credentials.password,
            }),
          })

          if (!response.ok) {
            return null
          }

          const data = await response.json()

          if (!data.user || !data.user.id) {
            return null
          }

          // Return user object with JWT token and expiration
          // Backend JWT_EXPIRATION_MS is 86400000 (24 hours)
          return {
            id: data.user.id.toString(),
            email: data.user.email,
            name: `${data.user.firstName} ${data.user.lastName}`,
            accessToken: data.token,
            accessTokenExpires: Date.now() + 86400000, // 24 hours in ms
          }
        } catch {
          return null
        }
      },
    }),
  ],
  callbacks: {
    async jwt({ token, user }) {
      // Initial sign in
      if (user) {
        token.accessToken = user.accessToken
        token.accessTokenExpires = user.accessTokenExpires
        token.id = user.id
      }

      // Return previous token if it hasn't expired yet
      if (typeof token.accessTokenExpires === 'number' && Date.now() < token.accessTokenExpires) {
        return token
      }

      // Access token has expired, try to refresh it
      return refreshAccessToken(token)
    },
    async session({ session, token }) {
      // Send properties to the client
      if (token) {
        session.user.id = token.id as string
        session.accessToken = token.accessToken as string
      }
      return session
    },
  },
  pages: {
    signIn: "/login",
    error: "/login",
  },
  session: {
    strategy: "jwt",
    maxAge: 30 * 24 * 60 * 60, // 30 days
    updateAge: 24 * 60 * 60, // 24 hours - session extends every 24 hours
  },
  cookies: {
    sessionToken: {
      name: process.env.NODE_ENV === "production" 
        ? "__Secure-next-auth.session-token" 
        : "next-auth.session-token",
      options: {
        httpOnly: true,
        sameSite: "lax",
        path: "/",
        secure: process.env.NODE_ENV === "production",
      },
    },
  },
  useSecureCookies: process.env.NODE_ENV === "production",
  trustHost: true, // Required for Next.js 16
})
