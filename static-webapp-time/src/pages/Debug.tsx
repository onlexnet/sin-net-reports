import { getRuntimeConfig } from "@/config/RuntimeConfig"

export function Debug() {
  const config = getRuntimeConfig()
  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Debug</h1>
      <pre className="bg-muted p-4 rounded text-sm">
        {JSON.stringify(config, null, 2)}
      </pre>
    </div>
  )
}
