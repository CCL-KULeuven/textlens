# Annotate (general)
## No corpus selected / corpus deleted
- Create a corpus. Open any tab, e.g. annotate -> documents.
- Copy url
- Delete the corpus
- Paste url
- Should show a window directing the user to select a corpus.
- The same goes for when there is no "?corpus=..." in the url.

## Empty corpus
- Create an empty corpus
- Force navigate url to /galahad/annotate/jobs as well as /evaluate and /export
- Should display an error about empty corpus.

## Can't use eval & export tab with no source annotations
- Create a corpus.
- Upload (a) document(s) with no source annotations
- Try to open 'evaluate' and 'export'
- Should display an error about missing annotations.

## Can't select a layer with no annotations
- Create a corpus
- Upload a document **with no** source annotations
- Tag it, **wait for the tagger to finish before you continue**.
- Upload another document **with no** source annotations
- Upload a document **with** source annotations
- Open Annotate -> Evaluate. The tagger that was used and 'Source annotations' should appear in the **hypothesis** dropdown.
- Select the tagger in the **hypothesis** dropdown.
- Go to Annotate -> Jobs and delete the tagger job.
- Try to go back to Annotate -> Evaluate. Only source annotations should be visible. Select it.
- Go to Annotate -> Documents. Delete the document **with** source annotations from the corpus
- Open Annotate -> Evaluate. Should display an error about missing annotations.

### Also
- Obviously, when a tagger hasn't been run yet, it should not appear in the dropdowns



# Annotate -> Corpora
## Selecting corpora
- Selecting a corpus with no documents should disable the tabs: jobs, evaluate & export.
- Selecting a public corpus (specifically as a non-admin) should always disable the 'export' tab. (Unless you're an admin.)
- When selecting a public corpus (as a non-admin) the edit and delete buttons should be disabled.

## Creating/editing/deleting corpora
- The 'new', 'edit' & 'delete' button should appear under 'your corpora'.
- Only 'edit' should appear under 'shared with you'. (But admins see 'edit' & 'delete'.)
- No buttons under 'public corpora'. (But admins see 'edit' & 'delete'.)

## Creating new corpora while having active selections
- Be a non-admin
- Select a user corpus.
- Press 'new'.
- In the new corpus dialog, the collaborator & viewer list should be visible. The public & dataset checkmark should not be visible.
- Do the same thing while having a public corpus selected.

## Collaborators, viewers & owners
*A user can only be added as either a collaborator or a viewer. Collaborator takes precedence when a user appears in both lists (and they will be removed from the viewers list.)*

- Create a new corpus and set a certain username as viewer (remember to press add). Press 'create'.
- Edit the same corpus. The username should appear as viewer. Now, set the username as collaborator (remember to press add). Don't remove it under viewer. Press 'update'.
- Edit the same corpus. The username should now appear as collaborator. Add it as viewer. Don't remove it under collaborator. Press 'update'.
- Edit the same corpus. The username should still appear as collaborator.

## Rights system
- The rights system should work: https://github.com/INL/Galahad/issues/518

## Source url
- Create or edit a corpus
- Enter an incorrect url. This could be a malformed protocol (htps), wrong or missing slashes (http:\test.nl)
- TODO: document possible urls to test
- Some graceful error should be shown.



# Annotate -> Documents
## Uploaded files with errors
- Upload, for example, a TEI file with a missing closing xml tag, and a tsv file with no header.
- They should not appear in the documents table.
- An error is given for each incorrect file.
- Errors disappear on refresh/switching tabs

### Incorrectly formatted tei xml
- Should register as an error. (e.g. not closing a <body>)

### TSV without header
- Upload a tsv file without header.
- Error message should explain which columns Galahad expects.

## Wrong extensions
- Select a file with an extension that isn't allowed (deselect extension setting in windows explorer)
- A warning is shown.
- If still uploaded, an error is shown.

## Upload a zip file
- All files should appear after the upload, includings those in subdirectories (name clashes are not resolved.)

## Download and delete documents
- Should work, except when you're a viewer.

## Source layer preview
- Should show the first 20 tokens. Works even as a viewer.

## Sorting the table
- Should work

## Upload a large zip, trigger timeout
- HTTP request timeout after one minute by default. So we try to upload a very large zip.
- Should show an error message with explanation.



# Annotate -> Jobs
## Tagger name links
Clicking on a tagger name (displayed as links) should open overview -> taggers, with the respective tagger row highlighted in colour.

## Filters
All four filter options should work. (Currently, all taggers produce all three types, so that one can't be tested.)

## Filters result in no matches
When filters are set so that no taggers match, an explanational message should appear.

## Layer preview
When a job finishes, the layer preview should be loaded without the need to refresh the page.

## Uploading new files after tagging
- Upload and tag 1 document.
- Check if a layer preview of the tagged document is displayed once the job is finished (should not require a reload!).
- Upload a second document.
- Tagged layer preview should still be there (displaying only the first document). Additionaly, the 'start' button should be enabled once more.
- Delete the second document.
- Tagged layer preview should still be there.

## Jobs
- A job should be able to start, stop and delete.
### A bit harder to test
- Upload a document that takes, e.g., 10 minutes to tag.
- Start the tagger
- Stop the tagger after a couple of seconds.
- Start the tagger again.
- The job should be finished after 10 minutes, not 20. (I.e. the thread that was launched to tag the document was stopped upon pressing stop. Otherwise the tagger would wait for the first attempt to finish before tagging the second attempt.)



# Annotate -> Evaluate
## Force browsing
- Set a hypothesis & reference in the url parameter that do not exist. (e.g. galahad/annotate/evaluate/distribution?corpus=[ID]&hypothesis=invalid&reference=invalid)
- The annotation layer dropdown should be empty.
- You can now select an existing one.

## Distribution
- When the reference layer changes, the distribution tab should not refresh (after all it only depends on the hypothesis)

## Distribution
- Filters should work

## Distribution filters result in no matches
- When the distribution filters are set so that there are no matches, an explanational message should appear.

## Distribution table navigation
= When succesively going to the next page of the table, the screen and navigation buttons should not jump around.

## Export csv
- the columns in metrics.csv should appear in the same order as the metrics tab
- The rows of distribution.csv should be sorted by frequency first, alphabetically second.
- Missing match should appear as the last row and column in confusion.csv. Rows and columns should be sorted alphabetically.

## Export individual POS confusion
- Click on a button in the POS confusion table
- In the downloadable csv file, there should be 5 columns matching the preview table
- The number of rows should amtch the number on the button.

## Evaluate partial layer
- Tag a corpus and stop halfway or add some extra untagged documents afterwards.
- Select the tagger as a hypothesis or reference layer.
- Warning should notify user of partial layer.



# Annotate -> Export
## Force browsing annotation layers
- Set a hypothesis in the url parameter that does not exist. (e.g. /galahad/annotate/export?corpus=[ID]&hypothesis=invalid)
- The annotation layer dropdown should be empty.
- You can now select an existing one.

## Force browsing datasets
- Be a non-admin
- Select a dataset corpus
- Force navigate to /galahad/annotate/export?corpus=[DATASET-ID]
- Should display a message about insufficient rights.

## Export partial layer
- Tag a corpus and stop halfway or add some extra untagged documents afterwards.
- Select the tagger as export layer.
- Warning should notify user of partial export.

## Merge TEI while exporting.
- Upload and tag at least one TEI-p5 file. Add some other files like tsv or txt as well.
- Export the tagged layer as TEI
- A merge button should appear
- Confirm the the one TEI-p5 has retained its structure (linebreaks will appear differently, but all xml tags should be present). Any other files should have been exported as TEI as well.

## Export non-TEI as TEI
- Upload and tag non-TEI files
- Export it as TEI
- No merge button should appear.

## Merge TSV while exporting
- Create a corpus with at least one tsv file that has extra columns. For example, copy this:
```
token	id	lemma	notes	pos	value
ik	1	null	12345	no-pos	true
loop	2	null	cool!	no-pos	false
naar	3	null	a b c	no-pos	false
school	4	null	okay?	no-pos	true
```
- Tag it. Go to Export. Select the tagger as layer. Select tsv as format.
- A message should appear explaining about the 'merge' option. Click the merge checkbox.
- Export and check whether the export went as expected. Any other files should have been exported as TSV as well.

## Extensions
- Exporting should replace the original extension. E.g. `test.tsv` -> `test.xml`. Wrong: `test.tsv.xml`



# Overview -> Datasets
## Non-dataset query parameter
- Go to Annotate -> Corpora, and select a corpus that is not a dataset (a user corpus or a non-dataset public corpus).
- Navigate to Overview -> Datasets
- The documents table should say "No corpus selected." (it should not display docs from user corpora)

## Documents table as viewer
- When selecting a dataset, the documents table should appear with viewer only rights (no download, delete or upload docs button)



# Overview -> Evaluate
## Non-dataset query parameter
- Go to Annotate -> Corpora, and select a corpus that is not a dataset (a user corpus or a non-dataset public corpus).
- Overview -> Evaluate
- The query parameter 'corpus' should disappear. Now you can select a dataset from the dropdown menu.

## Switching dataset
- Switching dataset should reset hypothesis, reference and the 3 evaluation tabs



# Application wide tests
## Url query parameters
- Refresh on each page, with selections. The parameters in the url (corpus, hypothesis, reference) should be loaded into the application.

### Annotate
- Select a corpus in Annotate -> Corpus
- Go to evaluate and select a hypothesis and reference job
- Refresh on each of the 5 annotate subtabs

### Overview
- Select a dataset, hypothesis and reference in Overview -> Evaluate
- Refresh on each of the 4 subtabs

## Small screen
- All content should be accessible on small browser windows.

## 404
- visit an invalid page url (/galahad/invalid)
- 404

## Admin
- If you are an admin it should say so on the bottom of the user page.
