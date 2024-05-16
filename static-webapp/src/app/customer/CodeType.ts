export type CodeType = CodeUndefined | CodeTotp

export type CodeUndefined = {
    type: 'UNDEFINED'
}

export type CodeTotp = {
    type: 'TOTP'
    code: string
    secondsLeft: number
}