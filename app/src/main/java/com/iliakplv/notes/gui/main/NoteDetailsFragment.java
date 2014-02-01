package com.iliakplv.notes.gui.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.iliakplv.notes.BuildConfig;
import com.iliakplv.notes.R;
import com.iliakplv.notes.notes.AbstractNote;
import com.iliakplv.notes.notes.TextNote;
import com.iliakplv.notes.notes.db.NotesDatabaseEntry;
import com.iliakplv.notes.notes.db.NotesDatabaseFacade;
import com.iliakplv.notes.utils.StringUtils;

/**
 * Author: Ilya Kopylov
 * Date:  16.08.2013
 */
public class NoteDetailsFragment extends Fragment {

	private static final String LOG_TAG = NoteDetailsFragment.class.getSimpleName();

	final static String ARG_NOTE_ID = "note_id";

	private int noteId = MainActivity.NO_DETAILS;
	private final NotesDatabaseFacade dbFacade = NotesDatabaseFacade.getInstance();
	private NotesDatabaseEntry<AbstractNote> noteEntry;

	private View dualPaneDetails;
	private View dualPaneSeparator;
	private EditText title;
	private EditText body;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.note_details, container, false);
		title = (EditText) view.findViewById(R.id.note_title);
		body = (EditText) view.findViewById(R.id.note_body);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		final Bundle args = getArguments();
		if (args != null) {
			noteId = args.getInt(ARG_NOTE_ID);
		}
		updateNoteDetailsView(noteId);
	}


	public void updateNoteDetailsView(int noteId) {
		// try save changes on previously shown note
		trySaveCurrentNote();

		// show new note
		this.noteId = noteId;
		noteEntry = noteId > 0 ? dbFacade.getNote(noteId) : null;
		final boolean gotNoteToShow = noteEntry != null;

		dualPaneDetails = getActivity().findViewById(R.id.note_details_fragment);
		dualPaneSeparator = getActivity().findViewById(R.id.dual_pane_fragments_separator);
		showDetailsPane(gotNoteToShow);
		if (gotNoteToShow) {
			final AbstractNote note = noteEntry.getEntry();
			title.setHint(NotesListFragment.getTitleForNote(TextNote.EMPTY, noteEntry.getId()));
			title.setText(note.getTitle());
			body.setText(note.getBody());
		}
	}

	private void showDetailsPane(boolean show) {
		final int visibility = show ? View.VISIBLE : View.GONE;
		if (dualPaneDetails != null) {
			dualPaneDetails.setVisibility(visibility);
		}
		if (dualPaneSeparator != null) {
			dualPaneSeparator.setVisibility(visibility);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// try save changes
		trySaveCurrentNote();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(ARG_NOTE_ID, noteId);
	}

	private void trySaveCurrentNote() {
		final String LOG_PREFIX = "trySaveCurrentNote(): ";
		if (BuildConfig.DEBUG) {
			Log.d(LOG_TAG, LOG_PREFIX + "call");
		}
		if (noteEntry != null) {
			final String newTitle = title.getText().toString();
			final String newBody = body.getText().toString();
			final AbstractNote currentNote = noteEntry.getEntry();
			if (!StringUtils.equals(currentNote.getBody(), newBody) ||
					!StringUtils.equals(currentNote.getTitle(), newTitle)) {
				currentNote.setTitle(newTitle);
				currentNote.setBody(newBody);
				currentNote.updateChangeTime();
				final boolean updated = dbFacade.updateNote(noteEntry.getId(), currentNote);
				if (BuildConfig.DEBUG) {
					Log.d(LOG_TAG, LOG_PREFIX + "Note data changed. Database " + (updated ? "" : "NOT ") + "updated");
				}
			} else {
				if (BuildConfig.DEBUG) {
					Log.d(LOG_TAG, LOG_PREFIX + "Note data unchanged. End");
				}
			}
		} else {
			if (BuildConfig.DEBUG) {
				Log.d(LOG_TAG, LOG_PREFIX + "Note entry is null. End");
			}
		}
	}
}
