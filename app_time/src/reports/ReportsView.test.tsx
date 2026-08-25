import React from "react";
import { act, render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

jest.mock("../addressProvider", () => ({
  addressProvider: () => ({ host: "https://example.com" })
}));

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
    expect(screen.getByRole("button", { name: /raport miesięczny \(eksperymentalne\) - załączniki do faktur/i })).toHaveClass("text-foreground");
    expect(screen.getByRole("button", { name: /zestawienie sumaryczne godzin/i })).toHaveClass("text-foreground");
    expect(screen.getByRole("button", { name: /lista klientów przypisanych do operatorów/i })).toHaveClass("text-foreground");
  });

  it("shows the experimental report for all users", () => {
    render(
      <ReportsView
        projectId="proj-1"
        idToken="token"
        from={{ year: 2026, month: 4, day: 1 }}
      />
    );

    expect(screen.getByRole("button", { name: /raport miesięczny \(eksperymentalne\)/i })).toBeInTheDocument();
  });

  it("shows a loading state while a report download starts", async () => {
    const user = userEvent.setup();
    const openSpy = jest.spyOn(window, "open").mockImplementation(() => null);

    let resolveFetch: (value: Response) => void = () => undefined;
    const fetchPromise = new Promise<Response>((resolve) => {
      resolveFetch = resolve;
    });

    const fetchSpy = jest.spyOn(global, "fetch" as any).mockReturnValue(fetchPromise as any);

    render(
      <ReportsView
        projectId="proj-1"
        idToken="token"
        from={{ year: 2026, month: 4, day: 1 }}
      />
    );

    await user.click(screen.getByRole("button", { name: /raport miesięczny \(eksperymentalne\) - załączniki do faktur/i }));

    await waitFor(() => {
      expect(openSpy).not.toHaveBeenCalled();
      expect(screen.getByRole("button", { name: /pobieranie raportu/i })).toBeInTheDocument();
    });

    await act(async () => {
      resolveFetch({
        ok: true,
        json: async () => ({ url: "https://example.com/report.pdf", expires_at: "2026-01-01T00:00:00Z" })
      } as Response);
    });

    await waitFor(() => {
      expect(openSpy).toHaveBeenCalled();
    });

    openSpy.mockRestore();
    fetchSpy.mockRestore();
  });
});
