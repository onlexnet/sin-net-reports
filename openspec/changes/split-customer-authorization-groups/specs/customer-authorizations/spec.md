# Spec Delta

## Purpose

Manage customer credentials in separate SinNet and customer authorization groups while preserving existing credential features, data, and change metadata.

## ADDED Requirements

### Requirement: Ordered authorization sections

The customer form SHALL display "Autoryzacje SinNet" followed by "Autoryzacje Klienta". Both sections SHALL remain visible when empty, and each authorization SHALL appear only in its assigned section.

#### Scenario: Customer has authorizations in both groups

- **WHEN** a user opens a customer with SinNet and customer authorizations
- **THEN** SinNet authorizations appear under "Autoryzacje SinNet"
- **AND** customer authorizations appear under "Autoryzacje Klienta" below the SinNet section
- **AND** neither section displays the other group's entries

#### Scenario: Empty authorization groups

- **WHEN** a user opens a customer with no authorizations
- **THEN** both section headings and their add-authorization controls are visible in the required order

### Requirement: Equivalent credential functionality

Both groups SHALL offer the same existing portal choices, standard and extended credential formats, editable fields, and add/edit/remove operations. Extended credentials SHALL retain organization name and provider code fields.

#### Scenario: Add either credential format to either group

- **WHEN** a user selects an existing portal choice in either section
- **THEN** a new authorization appears in that section using the portal's existing standard or extended format
- **AND** its editable fields match those available for the same portal in the other section

#### Scenario: Independently edit and remove credentials

- **GIVEN** both groups contain authorizations for the same portal
- **WHEN** a user edits or removes an authorization in one section and saves the customer
- **THEN** only the selected authorization is changed or removed
- **AND** the authorization in the other section is preserved

### Requirement: Equivalent TOTP functionality

Both groups SHALL support optional TOTP seeds and recovery keys. For a valid seed, the form SHALL display a six-digit SHA-1 TOTP with a 30-second period and remaining validity, using the existing missing-seed and invalid-seed feedback.

#### Scenario: TOTP in both formats and groups

- **WHEN** a standard or extended authorization in either group has a valid TOTP seed
- **THEN** the form displays its current TOTP code and remaining validity
- **AND** the seed and recovery keys remain editable and survive a customer save and reload

#### Scenario: Missing or invalid TOTP seed

- **WHEN** an authorization in either group has no TOTP seed or an invalid seed
- **THEN** the form displays the corresponding existing feedback without preventing other authorizations from being edited

### Requirement: Persistent independent group membership

Each authorization SHALL belong to exactly one of the SinNet or customer groups. Customer reads and writes SHALL preserve that membership for both credential formats, including otherwise identical entries in different groups.

#### Scenario: Save and reopen a mixed customer

- **WHEN** a user saves a customer containing standard and extended authorizations in both groups and reopens it
- **THEN** every authorization retains its group and credential fields

#### Scenario: Identical credentials in different groups

- **WHEN** a customer is saved with identical credential fields in both groups
- **THEN** both authorizations remain present and independently editable after reload

#### Scenario: Unrelated customer changes

- **WHEN** a user changes non-authorization customer details and saves the customer
- **THEN** all authorizations retain their group membership and credential data

#### Scenario: Invalid group input

- **WHEN** a customer write supplies an unsupported authorization group
- **THEN** the system rejects the write with an explicit error
- **AND** does not silently assign the entry to another group

### Requirement: Legacy authorization compatibility

All authorizations stored before grouping SHALL belong to SinNet after migration, with credential fields and existing change metadata preserved. A legacy authorization payload that omits grouping SHALL mean SinNet, not the customer group.

#### Scenario: Upgrade an existing customer

- **WHEN** a customer containing pre-grouping standard and extended authorizations is loaded after migration
- **THEN** all existing authorizations appear under "Autoryzacje SinNet"
- **AND** "Autoryzacje Klienta" is empty
- **AND** existing credential values and last-change metadata are unchanged

#### Scenario: Legacy payload without a group

- **WHEN** an authorization payload without grouping is accepted through the customer API
- **THEN** its authorization is assigned to SinNet

### Requirement: Group-aware last-change metadata

For both credential formats and groups, newly added or edited authorizations SHALL record the saving user's identity and the change time. Unchanged authorizations SHALL retain their previous metadata without matching entries from another group.

#### Scenario: Change one group while preserving the other

- **GIVEN** a customer has authorizations in both groups
- **WHEN** a user adds or edits an authorization in one group and saves
- **THEN** that authorization records the saving user and change time
- **AND** unchanged authorizations in either group retain their previous metadata

#### Scenario: Identical entries have independent metadata

- **GIVEN** both groups contain identical credentials with different last-change metadata
- **WHEN** the customer is saved without changing the credentials
- **THEN** each authorization retains its own group's previous metadata
