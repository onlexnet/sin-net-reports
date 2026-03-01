import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { getDisplayVersion } from "@/config/BuildInfo"

interface TestLoginProps {
  onLogin: (email: string) => void
}

export function TestLogin({ onLogin }: TestLoginProps) {
  const [email, setEmail] = useState("")
  const [loading, setLoading] = useState(false)

  const handleLogin = () => {
    if (!email || !email.includes("@")) {
      alert("Please enter a valid email address")
      return
    }
    setLoading(true)
    try {
      onLogin(email)
    } finally {
      setLoading(false)
    }
  }

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      handleLogin()
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-background">
      <Card className="w-[350px]">
        <CardHeader>
          <CardTitle>Test Login</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="email">Email Address</Label>
            <Input
              id="email"
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              onKeyDown={handleKeyDown}
              disabled={loading}
            />
          </div>
          <Button
            className="w-full"
            onClick={handleLogin}
            disabled={loading}
          >
            {loading ? "Logging in..." : "Login"}
          </Button>
          <p className="text-sm text-muted-foreground text-center">
            {getDisplayVersion()}
          </p>
        </CardContent>
      </Card>
    </div>
  )
}
