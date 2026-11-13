import React from "react";
import { fireEvent, render, screen, within } from "@testing-library/react";
import { AuthorizationGroup, CustomerSecretExInput, CustomerSecretInput, GetCustomerQuery, SaveCustomerMutationVariables } from "../../components/.generated/components";
import { rootReducer } from "../../store/reducers";
import { CustomerView, CustomerViewEntry, SecretExModel, SecretModel } from "./CustomerView";
import { CustomerViewEditLocal } from "./CustomerView.Edit";
import SelectedCustomerView from "../actions/ActionView.Edit.CustomerView";
import { SecretsTimestamp } from "./SecretsTimestamp";

type CustomerSaveVariables = Omit<SaveCustomerMutationVariables, "secrets" | "secretsEx"> & {
    secrets: CustomerSecretInput[],
    secretsEx: CustomerSecretExInput[]
};
const mockSave = jest.fn((options: { variables: CustomerSaveVariables }) => Promise.resolve({}));
let mockCustomerData: GetCustomerQuery | undefined;

jest.mock("../../components/.generated/components", () => ({
    ...jest.requireActual("../../components/.generated/components"),
    useSaveCustomerMutation: () => [mockSave, { loading: false }],
    useRemoveCustomerMutation: () => [jest.fn(), {}],
    useGetCustomerQuery: () => ({ data: mockCustomerData })
}));
jest.mock("../../api/useGetUsers", () => ({ useGetUsers: () => [] }));
jest.mock("components/ui/select", () => {
    const React = require("react");
    return {
        Select: ({ children, onValueChange }: { children: React.ReactNode, onValueChange: (value: string) => void }) =>
            <select onChange={event => onValueChange(event.target.value)}>{children}</select>,
        SelectTrigger: () => null,
        SelectValue: () => null,
        SelectContent: ({ children }: { children: React.ReactNode }) => <>{children}</>,
        SelectItem: ({ children, value }: { children: React.ReactNode, value: string }) =>
            <option value={value}>{children}</option>
    };
});

const id = { projectId: "project", entityId: "customer", entityVersion: 1 };
const callbacks = { itemSaved: jest.fn(), itemRemoved: jest.fn() };
const section = (group: AuthorizationGroup) => screen.getByRole("region", {
    name: group === AuthorizationGroup.Sinnet ? "Autoryzacje SinNet" : "Autoryzacje Klienta"
});
const entry = (): CustomerViewEntry => ({
    KlientNazwa: "Clinic", autoryzacje: [], autoryzacjeEx: [], kontakty: []
});
const credential = (group: AuthorizationGroup): SecretModel => ({
    localKey: `standard-${group}`, authorizationGroup: group, location: "GUS",
    username: "user", password: "pass", otpSecret: "JBSWY3DPEHPK3PXP",
    otpRecoveryKeys: "recovery", who: `${group}@example.test`,
    when: SecretsTimestamp.of("2020-01-02T03:04:05")
});
const extendedCredential = (group: AuthorizationGroup): SecretExModel => ({
    ...credential(group), localKey: `extended-${group}`, location: "SIMP",
    entityName: "Branch", entityCode: "123"
});

const reloadFromSave = () => {
    const { variables } = mockSave.mock.calls[0][0];
    const customerName = variables.entry.customerName;
    if (!customerName) {
        throw new Error("Saved customer fixture requires a name");
    }
    mockCustomerData = {
        Customers: {
            get: {
                id,
                data: { ...variables.entry, customerName },
                contacts: [],
                secrets: variables.secrets.map(it => ({
                    ...it, authorizationGroup: it.authorizationGroup ?? AuthorizationGroup.Sinnet,
                    changedWho: "operator@example.test", changedWhen: "2020-01-02T03:04:05"
                })),
                secretsEx: variables.secretsEx.map(it => ({
                    ...it, authorizationGroup: it.authorizationGroup ?? AuthorizationGroup.Sinnet,
                    changedWho: "operator@example.test", changedWhen: "2020-01-02T03:04:05"
                }))
            }
        }
    };
};
const renderLoadedCustomer = () => render(<CustomerViewEditLocal
    {...rootReducer(undefined, { type: "APPCONTEXT_READY", projectId: id.projectId })}
    {...callbacks} id={id} />);

beforeEach(() => {
    mockSave.mockClear();
    mockCustomerData = undefined;
});

it("shows both empty sections in order and adds both formats to the selected group", () => {
    render(<CustomerView id={id} entry={entry()} {...callbacks} />);
    const sinnet = section(AuthorizationGroup.Sinnet);
    const customer = section(AuthorizationGroup.Customer);
    expect(sinnet.compareDocumentPosition(customer) & Node.DOCUMENT_POSITION_FOLLOWING).toBeTruthy();
    for (const group of [AuthorizationGroup.Sinnet, AuthorizationGroup.Customer]) {
        const view = within(section(group));
        const select = view.getByRole("combobox");
        fireEvent.change(select, { target: { value: "GUS" } });
        fireEvent.change(select, { target: { value: "SIMP" } });
        expect(view.getAllByText("GUS")).toHaveLength(2);
        expect(view.getAllByText("SIMP")).toHaveLength(2);
        expect(view.getAllByPlaceholderText("U\u017cytkownik")).toHaveLength(2);
    }
    fireEvent.click(screen.getByRole("button", { name: "Zapisz i wyjd\u017a" }));
    const { variables } = mockSave.mock.calls[0][0];
    expect(variables.secrets.map(it => [it.location, it.authorizationGroup])).toEqual([
        ["GUS", AuthorizationGroup.Sinnet], ["GUS", AuthorizationGroup.Customer]
    ]);
    expect(variables.secretsEx.map(it => [it.location, it.authorizationGroup])).toEqual([
        ["SIMP", AuthorizationGroup.Sinnet], ["SIMP", AuthorizationGroup.Customer]
    ]);
});

it("preserves identical cross-group credentials through edits, saves, and actual detail loading", () => {
    const initial = entry();
    initial.autoryzacje = [credential(AuthorizationGroup.Sinnet), credential(AuthorizationGroup.Customer)];
    initial.autoryzacjeEx = [extendedCredential(AuthorizationGroup.Sinnet), extendedCredential(AuthorizationGroup.Customer)];
    const { unmount } = render(<CustomerView id={id} entry={initial} {...callbacks} />);
    const customer = within(section(AuthorizationGroup.Customer));
    fireEvent.change(customer.getAllByPlaceholderText("Has\u0142o")[0], { target: { value: "new-extended" } });
    fireEvent.change(customer.getAllByPlaceholderText("Has\u0142o")[1], { target: { value: "new-standard" } });
    fireEvent.change(screen.getByPlaceholderText("Nazwa klienta"), { target: { value: "Renamed clinic" } });
    expect(customer.getAllByDisplayValue("CUSTOMER@example.test")).toHaveLength(2);
    fireEvent.click(screen.getByRole("button", { name: "Zapisz i wyjd\u017a" }));

    const { variables } = mockSave.mock.calls[0][0];
    expect(variables.secrets.map(it => [it.authorizationGroup, it.password, it.otpSecret, it.otpRecoveryKeys]))
        .toEqual([
            [AuthorizationGroup.Sinnet, "pass", "JBSWY3DPEHPK3PXP", "recovery"],
            [AuthorizationGroup.Customer, "new-standard", "JBSWY3DPEHPK3PXP", "recovery"]
        ]);
    expect(variables.secretsEx.map(it => [it.authorizationGroup, it.password, it.entityName, it.entityCode]))
        .toEqual([
            [AuthorizationGroup.Sinnet, "pass", "Branch", "123"],
            [AuthorizationGroup.Customer, "new-extended", "Branch", "123"]
        ]);
    reloadFromSave();
    unmount();
    renderLoadedCustomer();
    expect(within(section(AuthorizationGroup.Sinnet)).getAllByDisplayValue("pass")).toHaveLength(2);
    expect(within(section(AuthorizationGroup.Customer)).getByDisplayValue("new-standard")).toBeInTheDocument();
    expect(within(section(AuthorizationGroup.Customer)).getByDisplayValue("new-extended")).toBeInTheDocument();
    expect(screen.getByDisplayValue("Renamed clinic")).toBeInTheDocument();
});

it("removes credentials only from the selected group and preserves the other on reload", () => {
    const initial = entry();
    initial.autoryzacje = [credential(AuthorizationGroup.Sinnet), credential(AuthorizationGroup.Customer)];
    initial.autoryzacjeEx = [extendedCredential(AuthorizationGroup.Sinnet), extendedCredential(AuthorizationGroup.Customer)];
    const { unmount } = render(<CustomerView id={id} entry={initial} {...callbacks} />);
    const customer = within(section(AuthorizationGroup.Customer));
    customer.getAllByRole("button", { name: "Usu\u0144" }).forEach(button => fireEvent.click(button));
    expect(customer.queryAllByPlaceholderText("Has\u0142o")).toHaveLength(0);
    expect(within(section(AuthorizationGroup.Sinnet)).getAllByPlaceholderText("Has\u0142o")).toHaveLength(2);
    fireEvent.click(screen.getByRole("button", { name: "Zapisz i wyjd\u017a" }));
    reloadFromSave();
    unmount();
    renderLoadedCustomer();
    expect(within(section(AuthorizationGroup.Customer)).queryAllByPlaceholderText("Has\u0142o")).toHaveLength(0);
    expect(within(section(AuthorizationGroup.Sinnet)).getAllByDisplayValue("pass")).toHaveLength(2);
});

it.each([AuthorizationGroup.Sinnet, AuthorizationGroup.Customer])(
    "keeps TOTP feedback and recovery-key editing in group %s",
    group => {
        const initial = entry();
        initial.autoryzacje = [{ ...credential(group), otpSecret: undefined }];
        initial.autoryzacjeEx = [{ ...extendedCredential(group), otpSecret: "!" }];
        render(<CustomerView id={id} entry={initial} {...callbacks} />);
        const view = within(section(group));
        expect(view.getByDisplayValue("TOTP nie zdefiniowany")).toBeInTheDocument();
        expect(view.getByDisplayValue(/Nieprawid.*sekret/)).toBeInTheDocument();
        fireEvent.change(view.getAllByPlaceholderText("TOTP secret")[0],
            { target: { value: "JBSWY3DPEHPK3PXP" } });
        fireEvent.change(view.getAllByPlaceholderText("TOTP secret")[1],
            { target: { value: "JBSWY3DPEHPK3PXP" } });
        expect(view.queryByDisplayValue(/Nieprawid.*sekret/)).not.toBeInTheDocument();
        expect(view.getAllByDisplayValue(/^\d{6}$/)).toHaveLength(2);
        fireEvent.change(view.getAllByPlaceholderText("TOTP recovery keys")[0],
            { target: { value: "new-recovery" } });
        fireEvent.click(screen.getByRole("button", { name: "Zapisz i wyjd\u017a" }));
        const { variables } = mockSave.mock.calls[0][0];
        expect(variables.secretsEx[0]).toMatchObject({
            authorizationGroup: group, otpSecret: "JBSWY3DPEHPK3PXP", otpRecoveryKeys: "new-recovery"
        });
        expect(variables.secrets[0]).toMatchObject({
            authorizationGroup: group, otpSecret: "JBSWY3DPEHPK3PXP", otpRecoveryKeys: "recovery"
        });
    }
);

it("keeps the selected-customer provider-code display SinNet-based", () => {
    mockCustomerData = {
        Customers: { get: {
            id, data: { customerName: "Clinic" }, contacts: [], secrets: [],
            secretsEx: [
                { location: "Portal \u015bwiadczeniodawcy", authorizationGroup: AuthorizationGroup.Customer,
                    entityCode: "CUSTOMER-CODE", changedWho: "operator", changedWhen: "2020-01-02T03:04:05" },
                { location: "Portal \u015bwiadczeniodawcy", authorizationGroup: AuthorizationGroup.Sinnet,
                    entityCode: "SINNET-CODE", changedWho: "operator", changedWhen: "2020-01-02T03:04:05" }
            ]
        } }
    };
    render(<SelectedCustomerView projectId={id.projectId} customerId={id.entityId} />);
    expect(screen.getByText("SINNET-CODE")).toBeInTheDocument();
    expect(screen.queryByText("CUSTOMER-CODE")).not.toBeInTheDocument();
});
