import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";

dayjs.extend(utc);

export interface SecretsTimestamp {
    value: string;
}

/** Build from UTC datetime, formatted to dd/MM/yyyy hh:mm */
export class SecretsTimestamp {
    public static of(utcString: string) : SecretsTimestamp {
        // more https://day.js.org/docs/en/display/format
        const local = dayjs.utc(utcString).local().format("DD/MM/YYYY HH:mm");
        return { value: local};
    }
}
