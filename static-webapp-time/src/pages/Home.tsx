import { getDisplayVersion } from "@/config/BuildInfo"

export function Home() {
  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Witaj w systemie ewidencji usług.</h1>
      <p className="text-muted-foreground">version: {getDisplayVersion()}</p>
    </div>
  )
}
