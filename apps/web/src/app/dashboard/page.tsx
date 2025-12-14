import { auth } from "@/auth"
import { redirect } from "next/navigation"
import { SignOutButton } from "@/components/auth/sign-out-button"

export default async function DashboardPage() {
  const session = await auth()

  if (!session) {
    redirect("/login")
  }

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-4">Dashboard</h1>
      <div className="space-y-4">
        <p>Welcome, {session.user?.name || session.user?.email}!</p>
        <p className="text-sm text-muted-foreground">
          User ID: {session.user?.id}
        </p>
        <p className="text-sm text-muted-foreground">
          Email: {session.user?.email}
        </p>
        <SignOutButton />
      </div>
    </div>
  )
}
