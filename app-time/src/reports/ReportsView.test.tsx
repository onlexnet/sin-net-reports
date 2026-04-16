import React from "react";
import { render, screen } from "@testing-library/react";

import { ReportsView } from "./ReportsView";

describe("ReportsView", () => {
  it("renders report links with a high-contrast text class", () => {
    render(
      <ReportsView
        projectId="proj-1"
        idToken="token"
        from={{ year: 2026, month: 4, day: 1 }}
      />
    );

    expect(screen.getByRole("button", { name: /raport miesięczny - załączniki do faktur/i })).toHaveClass("text-foreground");
    expect(screen.getByRole("button", { name: /zestawienie sumaryczne godzin/i })).toHaveClass("text-foreground");
    expect(screen.getByRole("button", { name: /lista klientów przypisanych do operatorów/i })).toHaveClass("text-foreground");
  });
});
