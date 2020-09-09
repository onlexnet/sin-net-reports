import { call, put, takeEvery, takeLatest } from 'redux-saga/effects';
import { reloadServicesBegin } from '../services/actions';
import { AddServiceCommand, ADD_SERVICE_COMMAND } from '../services/types';
import { sdk } from '../../api';
import { NewServiceActionMutation, NewServiceActionMutationVariables } from '../../api/generated';

const addService = function* (action: AddServiceCommand) {
    try {
        const result = (yield call(sdk.newServiceAction, {
            when: "2020/08/01",
            what: action.payload.description,
            who: action.payload.serviceMan,
            whom: action.payload.customerName
        } as NewServiceActionMutationVariables )) as NewServiceActionMutation;

        var addNewResult = result.Services.addNew;
        if (addNewResult) {
            yield put(reloadServicesBegin());
        }
    } catch (e) {
        yield put({ type: "USER_FETCH_FAILED", message: e.message });
    }
}

export function* addServiceSaga() {
    yield takeEvery(ADD_SERVICE_COMMAND, addService);
}
