package chenliu.madcourse.neu.edu.numad18s_chenliu.Scroggle;

/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/eband4 for more book information.
 ***/

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import chenliu.madcourse.neu.edu.numad18s_chenliu.R;


public class ScroggleMainFragment extends Fragment {

    private AlertDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.wordgame_fragment_main, container, false);
        // Handle buttons here...
        View newButton = rootView.findViewById(R.id.bt_wg_newgame);
        View continueButton = rootView.findViewById(R.id.bt_wg_continue);
        View quitButton = rootView.findViewById(R.id.bt_wg_exit);
        View ackButton = rootView.findViewById(R.id.bt_wg_ack);
        View howToPlayButton = rootView.findViewById(R.id.bt_wg_instruction);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), ScroggleGameActivity.class);
                getActivity().startActivity(intent);
            }
        });
        continueButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScroggleGameActivity.class);
                intent.putExtra(ScroggleGameActivity.KEY_RESTORE, true);
                getActivity().startActivity(intent);

            }


        });

        ackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up dialog
                final Dialog mDialog = new Dialog(getActivity());
                mDialog.setTitle("Acknowledgement");
                mDialog.setContentView(R.layout.scroggle_ack);
                mDialog.setCancelable(true);

                //set up text
                TextView text = (TextView) mDialog.findViewById(R.id.ack_scroggle);
                text.setText(R.string.scroggle_ack);

                Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                ok_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mDialog != null)
                            mDialog.dismiss();
                    }
                });
                //now that the dialog is set up, it's time to show it
                mDialog.show();
            }
        });

        howToPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up dialog
                final Dialog mDialog = new Dialog(getActivity());
                mDialog.setTitle("Instructions");
                mDialog.setContentView(R.layout.wordgame_instructions);
                mDialog.setCancelable(true);

                //set up text
                TextView text = (TextView) mDialog.findViewById(R.id.ins_button);
                text.setText(R.string.wd_ins);

                Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                ok_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mDialog != null)
                            mDialog.dismiss();
                    }
                });
                //now that the dialog is set up, it's time to show it
                mDialog.show();
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().finish();
            }
        });
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        // Get rid of the about dialog if it's still up
        if (mDialog != null)
            mDialog.dismiss();
    }
}
