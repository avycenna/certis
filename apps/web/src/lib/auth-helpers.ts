import { auth } from "@/auth"

/**
 * Server-side function to get the current session and access token
 * Use in Server Components and Server Actions
 */
export async function getServerSession() {
  const session = await auth()
  return session
}

/**
 * Server-side function to get the access token
 */
export async function getAccessToken() {
  const session = await auth()
  return session?.accessToken
}

/**
 * Server-side function to check if user is authenticated
 */
export async function isAuthenticated() {
  const session = await auth()
  return !!session
}
