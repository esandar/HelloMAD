package chenliu.madcourse.neu.edu.numad18s_chenliu.AnimalSudoku;


import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;

public class ASTile {
    private View mView;
    private ASTile mSubTiles[];

    //
    private int number;
    private status status;

    public enum status {
        // status of tiles on the board
        LOCKED, AVAILABLE, FILLED_PASS, FILLED_FAIL,

        // status of tiles on the left side
        NUMBER_SELECTED, NUMBER_NOT_SELECTED
    }
    private static final int LEVEL_LOCKED = 0;
    private static final int LEVEL_EMPTY = 1;
    private static final int LEVEL_FILLED_PASS = 2;
    private static final int LEVEL_FILLED_FAIL = 3;
    private static final int LEVEL_NUMBER_SELECTED = 4;
    private static final int LEVEL_NUMBER_NOT_SELECTED = 5;

    public ASTile(int number, status status) {
        this.number = number;
        this.status = status;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        this.mView = view;
    }

    public ASTile[] getSubTiles() {
        return mSubTiles;
    }

    public void setSubTiles(ASTile[] subTiles) {
        this.mSubTiles = subTiles;
    }

    public void updateDrawableState() {
        if (mView == null) return;
        if (mView.getBackground() != null) {
            mView.getBackground().setLevel(getStatusLevel());
        }
        if (mView instanceof ImageButton) {
            Drawable drawable = ((ImageButton) mView).getDrawable();
            drawable.setLevel(number);
        }
    }

    private int getStatusLevel() {
        int statusLevel;
        switch (status) {
            case LOCKED:
                statusLevel = LEVEL_LOCKED;
                break;
            case AVAILABLE:
                statusLevel = LEVEL_EMPTY;
                break;
            case FILLED_PASS:
                statusLevel = LEVEL_FILLED_PASS;
                break;
            case FILLED_FAIL:
                statusLevel = LEVEL_FILLED_FAIL;
                break;
            case NUMBER_SELECTED:
                statusLevel = LEVEL_NUMBER_SELECTED;
                break;
            case NUMBER_NOT_SELECTED:
                statusLevel = LEVEL_NUMBER_NOT_SELECTED;
                break;
            default:
                statusLevel = LEVEL_LOCKED;
                break;
        }
        return statusLevel;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ASTile.status getStatus() {
        return status;
    }

    public void setStatus(ASTile.status status) {
        this.status = status;
    }
}