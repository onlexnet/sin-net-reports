import { useState, useEffect } from "react"
import { BrowserRouter, Routes, Route } from "react-router-dom"
import { loadRuntimeConfig } from "@/config/RuntimeConfig"
import { TestLogin } from "@/pages/TestLogin"
import { NavBar } from "@/components/NavBar"
import { Home } from "@/pages/Home"
import { Services } from "@/pages/Services"
import { Customers } from "@/pages/Customers"
import { Debug } from "@/pages/Debug"
import { NotFound } from "@/pages/NotFound"

type AppState = "loading" | "login" | "authenticated" | "error"

export default function App() {
  const [state, setState] = useState<AppState>("loading")
  const [errorMessage, setErrorMessage] = useState<string>("")

  useEffect(() => {
    loadRuntimeConfig()
      .then((config) => {
        if (config.useTestLogin) {
          setState("login")
        } else {
          // In non-test mode, still show login for now
          setState("login")
        }
      })
      .catch((err: unknown) => {
        const msg = err instanceof Error ? err.message : String(err)
        setErrorMessage(msg)
        setState("error")
      })
  }, [])

  const handleLogin = (_email: string) => {
    setState("authenticated")
  }

  if (state === "loading") {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-muted-foreground">Loading...</p>
      </div>
    )
  }

  if (state === "error") {
    return (
      <div className="min-h-screen flex items-center justify-center p-8">
        <div className="text-center">
          <h1 className="text-2xl font-bold mb-4">Application Error</h1>
          <p className="text-muted-foreground">{errorMessage}</p>
        </div>
      </div>
    )
  }

  if (state === "login") {
    return <TestLogin onLogin={handleLogin} />
  }

  return (
    <BrowserRouter>
      <div className="flex min-h-screen">
        <NavBar />
        <main className="flex-1">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/services" element={<Services />} />
            <Route path="/customers" element={<Customers />} />
            <Route path="/debug" element={<Debug />} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  )
}
