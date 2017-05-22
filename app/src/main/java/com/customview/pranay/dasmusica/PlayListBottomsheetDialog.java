package com.customview.pranay.dasmusica;

import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;

/**
 * Created by Pranay on 18/05/2017.
 */

public class PlayListBottomsheetDialog extends BottomSheetDialogFragment {

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.playlist_dialog, null);
        dialog.setContentView(contentView);
    }
}
