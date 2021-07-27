import { Label, Separator, TextField } from "@fluentui/react"
import React from "react"

interface ViewProps {
  projectId: string | undefined,
  customerName: string | undefined
}

const View: React.FC<ViewProps> = props => {
  return (
    <div className="ms-Grid-col ms-sm12">
      <div className="ms-Grid-row">
        <div className="ms-Grid-col ms-sm12">
          <Separator alignContent="center">Dane wybranego klienta:</Separator>
        </div>
        <div className="ms-Grid-col ms-sm12">
          <Label>Nazwa</Label>
          <TextField readOnly value="(tu będzie pełna nazwa klienta))" />
        </div>
      </div>
    </div>
  )
}

export default View;