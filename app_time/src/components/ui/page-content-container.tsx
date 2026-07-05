import React from "react";
import { cn } from "lib/utils";

interface PageContentContainerProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
}

export const PageContentContainer: React.FC<PageContentContainerProps> = ({ children, className, ...props }) => {
  return (
    <div className={cn("w-full px-4", className)} {...props}>
      {children}
    </div>
  );
};
