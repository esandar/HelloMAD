package chenliu.madcourse.neu.edu.numad18s_chenliu.AnimalSudoku;

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


public class AS_MainFragment extends Fragment {

    private AlertDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.as_fragment_main, container, false);
        // Handle buttons here...
        View newButton = rootView.findViewById(R.id.bt_as_newgame);
        View progressButton = rootView.findViewById(R.id.bt_as_progress);
        View cheatButton = rootView.findViewById(R.id.bt_as_cheat);
        View ackButton = rootView.findViewById(R.id.bt_as_ack);
        View howToPlayButton = rootView.findViewById(R.id.bt_as_instruction);
        View aboutButton = rootView.findViewById(R.id.bt_as_about);

        newButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AS_GameActivity.class);
                getActivity().startActivity(intent);
            }

        });
        progressButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AS_ProgressActivity.class);
                getActivity().startActivity(intent);

            }


        });

        ackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.as_acknowledgement_label);
                builder.setMessage(R.string.as_ack);
                builder.setCancelable(false);
                builder.setPositiveButton(
                        R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // nothing
                            }
                        }
                );
                mDialog = builder.show();
            }
        });

        howToPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.as_tutorial_label);
                builder.setMessage(R.string.as_ins);
                builder.setCancelable(false);
                builder.setPositiveButton(
                        R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // nothing
                            }
                        }
                );
                mDialog = builder.show();
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.as_about_label);
                builder.setMessage(R.string.as_about);
                builder.setCancelable(false);
                builder.setPositiveButton(
                        R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // nothing
                            }
                        }
                );
                mDialog = builder.show();
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
