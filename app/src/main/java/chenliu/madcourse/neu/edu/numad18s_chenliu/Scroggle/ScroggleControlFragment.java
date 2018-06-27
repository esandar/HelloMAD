package chenliu.madcourse.neu.edu.numad18s_chenliu.Scroggle;

/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/eband4 for more book information.
 ***/

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import chenliu.madcourse.neu.edu.numad18s_chenliu.R;
import chenliu.madcourse.neu.edu.numad18s_chenliu.UTTT.GameActivity;

public class ScroggleControlFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.wordgame_fragment_control, container, false);
        View main = rootView.findViewById(R.id.bt_wd_main);
        View restart = rootView.findViewById(R.id.bt_wd_restart);
        View sound = rootView.findViewById(R.id.bt_wd_sound);
        View pause = rootView.findViewById(R.id.bt_wd_pause);
        View submit = rootView.findViewById(R.id.bt_wd_submit);

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ScroggleGameActivity) getActivity()).restartGame();
            }
        });
        return rootView;
    }

}
