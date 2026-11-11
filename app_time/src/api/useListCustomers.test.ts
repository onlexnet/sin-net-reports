import { CustomerType, ListCustomersItem, useListCustomersMapper } from "./useListCustomers";
import { AuthorizationGroup } from "../components/.generated/components";

describe('useListCustomers', () => {

  it('should extract filtering properties', () => {
    const customerId = { projectId: 'my projectId', entityId: 'my entityId', entityVersion: 101 };
    const given: CustomerType = {
      id: customerId,
      data: { customerName: 'my name' },
      secretsEx: [
        { authorizationGroup: AuthorizationGroup.Sinnet, location: 'my location1', entityCode: 'my code1' },
        { authorizationGroup: AuthorizationGroup.Customer, location: 'Portal świadczeniodawcy', entityCode: 'customer code' },
        { authorizationGroup: AuthorizationGroup.Sinnet, location: 'Portal świadczeniodawcy', entityCode: 'my code2' },
        { authorizationGroup: AuthorizationGroup.Sinnet, location: 'my location3', entityCode: 'my code3' }
      ]
    };
    var actual = useListCustomersMapper(given);
    var expected: ListCustomersItem = { customerId, name: 'my name', termNfzKodSwiadczeniodawcy: 'my code2' };
    expect(actual).toStrictEqual(expected);
  });
});
