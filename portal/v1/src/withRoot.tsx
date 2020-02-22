import React from "react";

function withRoot(Component: React.ComponentType) {
  const WithRoot: React.FC<{}> = (props: any) => {
    return <Component {...props} />;
  };

  return WithRoot;
}

export default withRoot;
