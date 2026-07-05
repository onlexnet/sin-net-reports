import React from "react";
import { cn } from "lib/utils";

interface TopCenteredContainerProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
}

export const TopCenteredContainer: React.FC<TopCenteredContainerProps> = ({ children, className, ...props }) => {
  return (
    <div className={cn("flex min-h-screen w-full items-start justify-center pt-8", className)} {...props}>
      {children}
    </div>
  );
};
