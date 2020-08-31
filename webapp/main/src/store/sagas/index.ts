import * as addServiceSagas from './addService';
import * as fetchServicesSagas from './reloadServices';
import * as loginUserSagas from './loginUser';
import * as changePeriodSagas from './changePeriod';
import { Saga } from 'redux-saga';

const sagas = {
    ...addServiceSagas,
    ...fetchServicesSagas,
    ...loginUserSagas,
    ...changePeriodSagas,
};

type sagaTypes = keyof typeof sagas;
export function registerWithMiddleware(middleware: { run: Function }) {
    for (var name in sagas) {
        const nameAsKey = name as sagaTypes;
        const saga = sagas[nameAsKey] as Saga;
        middleware.run(saga);
    }
}