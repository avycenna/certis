import { signOut } from "@/auth"

/**
 * Custom error class for API errors
 */
export class ApiError extends Error {
  constructor(
    message: string,
    public status: number,
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    public data?: any
  ) {
    super(message)
    this.name = "ApiError"
  }
}

/**
 * Fetches data from the Spring Boot API with authentication
 * Handles 401 errors by signing out the user (token refresh is handled by NextAuth JWT callback)
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

  const response = await fetch(url, {
    ...options,
    headers,
  })

  // Handle 401 Unauthorized - token expired and NextAuth couldn't refresh
  if (response.status === 401 && accessToken) {
    // Sign out user and redirect to login
    await signOut({ redirectTo: "/login" })
    throw new ApiError("Session expired. Please sign in again.", 401)
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
