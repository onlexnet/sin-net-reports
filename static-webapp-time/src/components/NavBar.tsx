import { Link, useLocation } from "react-router-dom"
import { cn } from "@/lib/utils"
import { Home, Users, Wrench, Bug } from "lucide-react"

const navItems = [
  { href: "/", label: "Strona główna", icon: Home },
  { href: "/services", label: "Usługi", icon: Wrench },
  { href: "/customers", label: "Klienci", icon: Users },
  { href: "/debug", label: "Debug", icon: Bug },
]

export function NavBar() {
  const location = useLocation()

  return (
    <nav className="h-screen w-48 bg-slate-800 text-white flex flex-col p-4 space-y-1">
      {navItems.map((item) => {
        const Icon = item.icon
        const isActive = location.pathname === item.href
        return (
          <Link
            key={item.href}
            to={item.href}
            className={cn(
              "flex items-center gap-3 px-3 py-2 rounded-md text-sm font-medium transition-colors",
              isActive
                ? "bg-slate-600 text-white"
                : "text-slate-300 hover:bg-slate-700 hover:text-white"
            )}
          >
            <Icon className="h-4 w-4" />
            {item.label}
          </Link>
        )
      })}
    </nav>
  )
}
