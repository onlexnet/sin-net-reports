import { call, put, takeEvery, takeLatest } from 'redux-saga/effects';
import { reloadServicesBegin } from '../services/actions';
import { AddServiceAction, ADD_SERVICE } from '../services/types';
import { sdk } from '../../api';
import { NewServiceActionMutation } from '../../api/generated';

const addService = function* (action: AddServiceAction) {
    try {
        const result = (yield call(sdk.newServiceAction, {
            when: "2020/08/01",
        })) as NewServiceActionMutation;

        var addNewResult = result.Services.addNew;
        if (addNewResult) {
            yield put(reloadServicesBegin(2020, 8));
        }
    } catch (e) {
        yield put({ type: "USER_FETCH_FAILED", message: e.message });
    }
}

export function* addServiceSaga() {
    yield takeEvery(ADD_SERVICE, addService);
}