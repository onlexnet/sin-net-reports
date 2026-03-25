import React from "react";
import { Separator } from "components/ui/separator";

export const HorizontalSeparatorStack = (props: { children: React.ReactNode }) => {
    const items = React.Children.toArray(props.children);

    return (
        <div className="space-y-3">
            {items.map((child, index) => (
                <React.Fragment key={index}>
                    {index > 0 && <Separator />}
                    {child}
                </React.Fragment>
            ))}
        </div>
    );
};
