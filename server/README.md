# Dev info
When debugging locally, add "profile=dev" to the environment variables (e.g. in the intelliJ debug configuration).
This makes it so that:
- taggers are called on localhost, instead of their docker container name via a docker network
- we use a test user, instead of retrieving it from the request headers

# Source code
The src/ folder contains the following packages:

## app
The base for spring boot and some application wide interfaces.

## data
All Galahad data is stored on disk. Hence why we have classes here like FileBackedCache and FileBackedValue.

## data.corpus

## data.layer
The annotations in a document (i.e. lemma and pos of each token) are collectively called a layer. A document can have multiple layers (as it can be tagged by multiple taggers). The original annotation layer is called the "sourceLayer".
A layer consists of a list of terms. A term consists of a lemma, a part of speech, and a token. At some point, the system was designed for a term to be able to point to multiple token, hence why Term in reality has a list of tokens, called "word forms". But in reality there is only ever one token, and multi word terms were not fully developed.

## evaluation
For evaluating a single layer (the frequency distribution) or comparing two layers (part of speech confusion and accuracy metrics), where one represents the absolute truth (called the "reference") and one is being tested against it (called the "hypothesis"). The main use case is setting the sourceLayer as the absolute truth reference.

To perform the evaluation, we compare layers, which requires us to compare the terms, which requires us to compare the word forms. The part of speech confusion and accuracy metrics then use these comparisons to construct an evaluation. They also keep track of 10 random samples for each evaluation. For example, for the %-incorrect metric of lemmata, 10 random samples are chosen that show a term with an incorrect lemma. And, for example, for the part of speech confusion, 10 samples are chosen to demonstrate the evaluation "noun vs verb". Etc.

In order to show a leaderboard of taggers on datasets, we have so-called 'assays'. These are simplified accuracy metrics.

## jobs
The process of a tagger tagging a document, which creates a new annotation layer, is called a job.

## port
Contains all document readers, converters and mergers for the supported formats in Galahad.

## tagset & tagger
Both relatively simple packages. Read out yaml files in a folder and make them available in a singleton-like manner.