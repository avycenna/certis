import { signOut } from "@/auth"

/**
 * Custom error class for API errors
 */
export class ApiError extends Error {
  constructor(
    message: string,
    public status: number,
    public data?: any
  ) {
    super(message)
    this.name = "ApiError"
  }
}

/**
 * Refresh the access token
 */
async function refreshToken(oldToken: string): Promise<string | null> {
  try {
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/auth/refresh`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(oldToken),
    })

    if (!response.ok) {
      return null
    }

    const data = await response.json()
    return data.token
  } catch {
    return null
  }
}

/**
 * Fetches data from the Spring Boot API with authentication
 * Automatically handles 401 errors by attempting token refresh
 * @param endpoint - API endpoint (e.g., "/users/me")
 * @param accessToken - JWT token from session
 * @param options - Fetch options
 */
export async function apiClient<T>(
  endpoint: string,
  accessToken?: string,
  options?: RequestInit
): Promise<T> {
  const url = `${process.env.NEXT_PUBLIC_API_URL}${endpoint}`

  const headers: HeadersInit = {
    "Content-Type": "application/json",
    ...(accessToken && { Authorization: `Bearer ${accessToken}` }),
    ...options?.headers,
  }

  let response = await fetch(url, {
    ...options,
    headers,
  })

  // Handle 401 Unauthorized - attempt token refresh and retry
  if (response.status === 401 && accessToken) {
    const newToken = await refreshToken(accessToken)
    
    if (newToken) {
      // Retry request with new token
      const newHeaders: HeadersInit = {
        "Content-Type": "application/json",
        Authorization: `Bearer ${newToken}`,
        ...options?.headers,
      }
      
      response = await fetch(url, {
        ...options,
        headers: newHeaders,
      })
    } else {
      // Refresh failed, sign out user
      await signOut({ redirectTo: "/login" })
      throw new ApiError("Session expired. Please sign in again.", 401)
    }
  }

  if (!response.ok) {
    let errorMessage = "Request failed"
    let errorData

    try {
      errorData = await response.json()
      errorMessage = errorData.message || errorData.error || errorMessage
    } catch {
      errorMessage = `Request failed with status ${response.status}`
    }

    throw new ApiError(errorMessage, response.status, errorData)
  }

  return response.json()
}

/**
 * Register a new user
 */
export async function registerUser(data: {
  firstName: string
  lastName: string
  email: string
  password: string
}) {
  return apiClient("/auth/register", undefined, {
    method: "POST",
    body: JSON.stringify(data),
  })
}

/**
 * Get current user profile
 */
export async function getCurrentUser(accessToken: string) {
  return apiClient("/users/me", accessToken)
}

/**
 * Update user profile
 */
export async function updateUser(
  accessToken: string,
  data: {
    firstName?: string
    lastName?: string
    phoneNumber?: string
  }
) {
  return apiClient("/users/me", accessToken, {
    method: "PUT",
    body: JSON.stringify(data),
  })
}

/**
 * Delete user account
 */
export async function deleteUser(accessToken: string) {
  return apiClient("/users/me", accessToken, {
    method: "DELETE",
  })
}
