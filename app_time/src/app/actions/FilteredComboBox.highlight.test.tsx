import React from "react";
import { fireEvent, render, screen } from "@testing-library/react";
import { FilteredComboBox, renderHighlightedText } from "./FilteredComboBox";

jest.mock("components/ui/input", () => ({
  Input: (props: any) => <input {...props} />,
}));

jest.mock("components/ui/select", () => ({
  Select: ({ children }: any) => <div>{children}</div>,
  SelectContent: ({ children }: any) => <div>{children}</div>,
  SelectItem: ({ children, value }: any) => <div data-testid={`item-${value}`}>{children}</div>,
  SelectTrigger: ({ children }: any) => <div>{children}</div>,
  SelectValue: ({ placeholder }: any) => <span>{placeholder}</span>,
}));

describe("FilteredComboBox highlight", () => {
  it("does not highlight when query is empty or whitespace", () => {
    const { rerender, container } = render(<>{renderHighlightedText("Poznan", "")}</>);
    expect(container.querySelector("span.font-medium")).toBeNull();

    rerender(<>{renderHighlightedText("Poznan", "   ")}</>);
    expect(container.querySelector("span.font-medium")).toBeNull();
  });

  it("highlights matched fragments case-insensitively", () => {
    const { container } = render(<>{renderHighlightedText("poz POZ Poz", "pOz")}</>);

    const highlights = container.querySelectorAll("span.font-medium");
    expect(highlights).toHaveLength(3);
    expect(Array.from(highlights).map(el => el.textContent)).toEqual(["poz", "POZ", "Poz"]);
  });

  it("renders matched fragment with highlight markup in FilteredComboBox list", () => {
    const onChange = jest.fn();
    const onSearch = jest.fn();

    const { container } = render(
      <FilteredComboBox
        selectedKey={undefined}
        items={[{ key: "1", text: "Centrum POZnan" }]}
        onChange={onChange}
        onSearch={onSearch}
      />
    );

    fireEvent.change(screen.getByPlaceholderText("Szukaj..."), { target: { value: "poz" } });

    const highlighted = container.querySelector("span.font-medium.bg-accent\\/30");
    expect(highlighted).not.toBeNull();
    expect(highlighted).toHaveTextContent("POZ");
    expect(onSearch).toHaveBeenCalledWith("poz");
  });
});
