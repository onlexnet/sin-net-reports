import React from "react";
import { render, screen } from "@testing-library/react";

import { ReportsView } from "./ReportsView";

describe("ReportsView", () => {
  it("renders report links with a high-contrast text class", () => {
    render(
      <ReportsView
        projectId="proj-1"
        idToken="token"
        email="siudeks@gmail.com"
        from={{ year: 2026, month: 4, day: 1 }}
      />
    );

    expect(screen.getByRole("button", { name: /raport miesięczny - załączniki do faktur/i })).toHaveClass("text-foreground");
    expect(screen.getByRole("button", { name: /raport miesięczny \(eksperymentalne\) - załączniki do faktur/i })).toHaveClass("text-foreground");
    expect(screen.getByRole("button", { name: /zestawienie sumaryczne godzin/i })).toHaveClass("text-foreground");
    expect(screen.getByRole("button", { name: /lista klientów przypisanych do operatorów/i })).toHaveClass("text-foreground");
  });

  it("hides the experimental report for other users", () => {
    render(
      <ReportsView
        projectId="proj-1"
        idToken="token"
        email="other@example.com"
        from={{ year: 2026, month: 4, day: 1 }}
      />
    );

    expect(screen.queryByRole("button", { name: /raport miesięczny \(eksperymentalne\)/i })).not.toBeInTheDocument();
  });

  it("shows the experimental report for biuro@sin.net.pl", () => {
    render(
      <ReportsView
        projectId="proj-1"
        idToken="token"
        email="biuro@sin.net.pl"
        from={{ year: 2026, month: 4, day: 1 }}
      />
    );

    expect(screen.getByRole("button", { name: /raport miesięczny \(eksperymentalne\)/i })).toBeInTheDocument();
  });
});
