package com.example.minesweeper2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.TableRow;

import androidx.appcompat.widget.AppCompatButton;

public class Block extends AppCompatButton {
    private boolean isCovered;
    private boolean isMined;
    private boolean isFlagged;
    private boolean isClickable;
    private int neighborMines;

    public void setDefaults()
    {
        isCovered = true;
        isMined = false;
        isFlagged = false;
        isClickable = true;
        neighborMines = 0;
    }

    public Block(Context context, int x, int y) {
        super(context);
        TableRow.LayoutParams layoutParams =
                new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1.0f);
        this.setLayoutParams(layoutParams);
    }

    public void setNumberOfNeighborMines(int number)
    {
        this.setBackgroundResource(R.drawable.square_grey);
        updateNumber(number);
    }

    public void setMineIcon(boolean enabled)
    {
        this.setText("*");

        if (!enabled)
        {
            this.setBackgroundResource(R.drawable.square_grey);
            this.setTextColor(Color.RED);
        }
        else
        {
            this.setTextColor(Color.BLACK);
        }
    }

    public void setFlagIcon(boolean enabled)
    {
        this.setText("+");

        if (!enabled)
        {
            this.setBackgroundResource(R.drawable.square_grey);
            this.setTextColor(Color.RED);
        }
        else
        {
            this.setTextColor(Color.BLACK);
        }
    }

    @SuppressLint("ResourceType")
    public void setBlockAsDisabled(boolean enabled)
    {
        if (!enabled)
        {
            this.setBackgroundResource(R.drawable.square_grey);
            setEnabled(false);
        }
        else
        {
            this.setBackgroundResource(Color.parseColor("#282828"));
        }
    }

    public void clearAllIcons()
    {
        this.setText("");
    }

    public void OpenBlock()
    {
        if (!isCovered)
            return;

        setBlockAsDisabled(false);
        isCovered = false;

        if (hasMine())
        {
            setMineIcon(false);
        }
        else
        {
            setNumberOfNeighborMines(neighborMines);
        }
    }

    public void updateNumber(int text)
    {
        if (text != 0)
        {
            this.setText(Integer.toString(text));
            switch (text)
            {
                case 1:
                    this.setTextColor(Color.BLUE);
                    break;
                case 2:
                    this.setTextColor(Color.rgb(0, 100, 0));
                    break;
                case 3:
                    this.setTextColor(Color.RED);
                    break;
                case 4:
                    this.setTextColor(Color.rgb(85, 26, 139));
                    break;
                case 5:
                    this.setTextColor(Color.rgb(139, 28, 98));
                    break;
                case 6:
                    this.setTextColor(Color.rgb(238, 173, 14));
                    break;
                case 7:
                    this.setTextColor(Color.rgb(47, 79, 79));
                    break;
                case 8:
                    this.setTextColor(Color.rgb(71, 71, 71));
                    break;
                case 9:
                    this.setTextColor(Color.rgb(205, 205, 0));
                    break;
            }
        }
    }

    public void plantMine()
    {
        isMined = true;
    }

    public void triggerMine()
    {
        setMineIcon(true);
        this.setTextColor(Color.RED);
    }
    public boolean isCovered()
    {
        return isCovered;
    }
    public boolean hasMine()
    {
        return isMined;
    }
    public void setNeighborMines(int number)
    {
        neighborMines = number;
    }
    public int getNeighborMines()
    {
        return neighborMines;
    }
    public boolean isFlagged()
    {
        return isFlagged;
    }
    public void setFlagged(boolean flagged)
    {
        isFlagged = flagged;
    }
    public boolean isClickable()
    {
        return isClickable;
    }
    public void setClickable(boolean clickable)
    {
        isClickable = clickable;
    }
}