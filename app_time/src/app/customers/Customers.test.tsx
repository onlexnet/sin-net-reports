import React from "react";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

import { CustomersView } from "./Customers";

describe("CustomersView", () => {
  it("renders the customer name header and the primary add action", () => {
    render(
      <MemoryRouter>
        <CustomersView
          givenProjectId="proj-1"
          onNewClientCommand={jest.fn()}
          listCustomers={() => [
            {
              customerId: {
                projectId: "proj-1",
                entityId: "cust-1",
                entityVersion: 1,
              },
              name: "ACME",
              termNfzKodSwiadczeniodawcy: "NFZ-001",
            },
          ]}
        />
      </MemoryRouter>
    );

    expect(screen.getByText("Nazwa")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /dodaj nowego klienta/i })).toBeInTheDocument();
    expect(screen.getByText("ACME")).toBeInTheDocument();
  });
});
