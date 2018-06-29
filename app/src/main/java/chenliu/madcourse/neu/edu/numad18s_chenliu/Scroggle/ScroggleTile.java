package chenliu.madcourse.neu.edu.numad18s_chenliu.Scroggle;

/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/eband4 for more book information.
 ***/

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import chenliu.madcourse.neu.edu.numad18s_chenliu.R;


public class ScroggleTile {

    public enum Owner {
        CLICKED, AVAILABLE, FROZEN, TAKEN, NA
    }
    //Define drawable definition
    private static final int LEVEL_AVAILABLE = 1; //green
    private static final int LEVEL_CLICKED = 3; //blue
    private static final int LEVEL_FROZEN = 2; //red
    private static final int LEVEL_TAKEN = 0; //grey
    private static final int LEVEL_NA = 0; //grey


    private final ScroggleGameFragment mGame;
    private ScroggleTile mSubTiles[];
    private View mView;
    private Owner mOwner = Owner.AVAILABLE;
    private String letter;

    public ScroggleTile(ScroggleGameFragment game) {
        this.mGame = game;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        this.mView = view;
    }

    public Owner getOwner() {
        return mOwner;
    }

    public void setOwner(Owner owner) {
        this.mOwner = owner;
    }

    public ScroggleTile[] getSubTiles() {
        return mSubTiles;
    }

    public void setSubTiles(ScroggleTile[] subTiles) {
        this.mSubTiles = subTiles;
    }

    public ScroggleTile deepCopy() {
        ScroggleTile tile = new ScroggleTile(mGame);
        tile.setOwner(getOwner());
        if (getSubTiles() != null) {
            ScroggleTile newTiles[] = new ScroggleTile[9];
            ScroggleTile oldTiles[] = getSubTiles();
            for (int child = 0; child < 9; child++) {
                newTiles[child] = oldTiles[child].deepCopy();
            }
            tile.setSubTiles(newTiles);
        }
        return tile;
    }

    public void animate() {
        Animator anim = AnimatorInflater.loadAnimator(mGame.getActivity(),
                R.animator.tictactoe);
        if (getView() != null) {
            anim.setTarget(getView());
            anim.start();
        }
    }

    public String getText() {
        Button b = (Button) mView;
        return b.getText().toString();
    }

    public void setText(String a) {
        Button b = (Button) mView;
        b.setText(String.valueOf(a));

    }
    public void updateDrawableState(char a, int x) {
        if (mView == null) return;
        int level = getLevel();
        if (mView.getBackground() != null) {
            mView.getBackground().setLevel(level);
        }
        if (mView instanceof Button) {
            if(x==1) {
                Button b = ((Button) mView);
                b.setText(String.valueOf(a));
            }
        }
    }

    private int getLevel() {
        int level = LEVEL_TAKEN;
        switch (mOwner) {
            case AVAILABLE:
                level = LEVEL_AVAILABLE;
                break;
            case CLICKED:
                level = LEVEL_CLICKED;
                break;
            case FROZEN:
                level = LEVEL_FROZEN;
                break;
            case NA:
                level = LEVEL_NA;
                break;
        }
        return level;
    }

}

