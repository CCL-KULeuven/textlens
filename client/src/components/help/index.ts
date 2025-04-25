/**
 * A collection of help components that can be looped over to display help for each section of the application.
 */

import intro from './Intro.vue'
import corpusHelp from './CorpusHelp.vue'
import startHelp from './StartHelp.vue'
import documentsHelp from './DocumentsHelp.vue'
import evaluateHelp from './EvaluateHelp.vue'
import exportHelp from './ExportHelp.vue'
import jobsHelp from './JobsHelp.vue'

const helpObject = {
    intro: intro,
    corpora: corpusHelp,
    start: startHelp,
    documents: documentsHelp,
    jobs: jobsHelp,
    evaluate: evaluateHelp,
    export: exportHelp,
} as const

export default helpObject;
export const order: Array<keyof typeof helpObject> = ['intro', 'start', 'corpora', 'documents', 'jobs', 'evaluate', 'export'];
