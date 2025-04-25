export type Assay = {
    name: string
    count: number
    bothAgree: number
    lemmaAgree: number
    lemmaDisagree: number
    posAgree: number
    posDisagree: number
    noMatch: number
}

export type AssayDescription = {
    id: string
    description: string
}

type TaggerName = string
type DatasetName = string
export type AssaysType = Record<TaggerName, Record<DatasetName, Assay>>
