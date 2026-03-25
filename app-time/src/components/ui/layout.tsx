import React from "react";
import { cn } from "lib/utils";

type Gutter = number | [number, number];
type RowAlign = "top" | "middle" | "bottom";
type RowJustify = "start" | "center" | "end" | "space-around" | "space-between" | "space-evenly";
type SpaceAlign = "start" | "center" | "end" | "baseline";
type SpaceSize = "small" | "middle" | "large" | number;

interface RowProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
  gutter?: Gutter;
  align?: RowAlign;
  justify?: RowJustify;
}

interface ColProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
  span?: number | string;
  offset?: number;
}

interface SpaceProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
  direction?: "horizontal" | "vertical";
  align?: SpaceAlign;
  size?: SpaceSize;
}

const toGap = (gutter: Gutter | undefined) => {
  if (Array.isArray(gutter)) {
    return { columnGap: gutter[0], rowGap: gutter[1] };
  }

  const value = gutter ?? 0;
  return { columnGap: value, rowGap: value };
};

const toSpan = (span: number | string | undefined) => {
  const parsed = Number(span);
  if (!Number.isFinite(parsed) || parsed <= 0) {
    return 24;
  }

  return Math.min(24, Math.max(1, parsed));
};

const toOffset = (offset: number | undefined) => {
  if (!offset || offset <= 0) {
    return 0;
  }

  return Math.min(23, offset);
};

const rowAlignStyles: Record<RowAlign, React.CSSProperties["alignItems"]> = {
  top: "start",
  middle: "center",
  bottom: "end",
};

const rowJustifyStyles: Record<RowJustify, React.CSSProperties["justifyContent"]> = {
  start: "start",
  center: "center",
  end: "end",
  "space-around": "space-around",
  "space-between": "space-between",
  "space-evenly": "space-evenly",
};

const spaceAlignClasses: Record<SpaceAlign, string> = {
  start: "items-start",
  center: "items-center",
  end: "items-end",
  baseline: "items-baseline",
};

const toSpaceGap = (size: SpaceSize | undefined) => {
  if (typeof size === "number") {
    return size;
  }

  switch (size) {
    case "small":
      return 8;
    case "large":
      return 24;
    case "middle":
    default:
      return 16;
  }
};

export const Row: React.FC<RowProps> = ({ children, gutter = 0, align, justify, className, style, ...props }) => {
  const { columnGap, rowGap } = toGap(gutter);

  return (
    <div
      className={cn("w-full", className)}
      style={{
        display: "grid",
        gridTemplateColumns: "repeat(24, minmax(0, 1fr))",
        columnGap,
        rowGap,
        alignItems: align ? rowAlignStyles[align] : undefined,
        justifyContent: justify ? rowJustifyStyles[justify] : undefined,
        ...style,
      }}
      {...props}
    >
      {children}
    </div>
  );
};

export const Col: React.FC<ColProps> = ({ children, span = 24, offset, className, style, ...props }) => {
  const resolvedSpan = toSpan(span);
  const resolvedOffset = toOffset(offset);

  return (
    <div
      className={cn("min-w-0", className)}
      style={{
        gridColumn: resolvedOffset > 0 ? `${resolvedOffset + 1} / span ${resolvedSpan}` : `span ${resolvedSpan} / span ${resolvedSpan}`,
        ...style,
      }}
      {...props}
    >
      {children}
    </div>
  );
};

export const Space: React.FC<SpaceProps> = ({
  children,
  direction = "horizontal",
  align = "center",
  size = "small",
  className,
  style,
  ...props
}) => {
  return (
    <div
      className={cn(
        "flex",
        direction === "vertical" ? "flex-col" : "flex-row flex-wrap",
        spaceAlignClasses[align],
        className,
      )}
      style={{
        gap: toSpaceGap(size),
        ...style,
      }}
      {...props}
    >
      {children}
    </div>
  );
};