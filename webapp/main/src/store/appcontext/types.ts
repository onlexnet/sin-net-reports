export type AppContext = AppContextReady | AppContextEmpty

interface AppContextEmpty {
    type: 'APPCONTEXT_EMPTY'
}

interface AppContextReady {
    type: 'APPCONTEXT_READY'
    projectId: string;   
}
