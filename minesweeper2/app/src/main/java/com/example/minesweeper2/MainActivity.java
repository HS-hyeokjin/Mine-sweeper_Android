package com.example.minesweeper2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TableLayout table;
    private Block[][] buttons;

    private TextView txtMineCount;
    private ToggleButton toggleButton;
    private int numberOfRowsInMineField = 9;
    private int numberOfColumnsInMineField = 9;
    private int totalNumberOfMines = 10;

    private boolean areMinesSet;
    private boolean isGameOver;
    private int remainBlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtMineCount = (TextView) findViewById(R.id.textView);
        TextView txtMine = (TextView) findViewById(R.id.textView2);
        txtMineCount.setText("10");

        areMinesSet = false;
        isGameOver = false;
        remainBlock = totalNumberOfMines;

        txtMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endExistingGame();
                startNewGame();
            }
        });

        table = (TableLayout)findViewById(R.id.tableLayout);

        createField();
        showField();

        toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String toastMessage;
                if(isChecked){
                    toastMessage = "          Flag (+)  모드          ";
                }else{
                    toastMessage = "         Block Break 모드         ";
                }
                Toast.makeText(getApplicationContext(),toastMessage,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startNewGame()
    {
        createField();
        showField();

        remainBlock = totalNumberOfMines;
        isGameOver = false;
    }

    private void showField() {
        for (int row = 1; row < numberOfRowsInMineField + 1; row++)
        {
            TableRow tableRow = new TableRow(this);
            for (int column = 1; column < numberOfColumnsInMineField + 1; column++)
            {
                tableRow.addView(buttons[row][column]);
            }
            table.addView(tableRow);
        }
    }

    private void endExistingGame()
    {
        txtMineCount.setText("10");
        table.removeAllViews();
        areMinesSet = false;
        isGameOver = false;
        remainBlock = 0;
    }

    private void createField()
    {
        buttons = new Block[numberOfRowsInMineField + 2][numberOfColumnsInMineField + 2];

        for (int row = 0; row < numberOfRowsInMineField + 2; row++)
        {
            for (int column = 0; column < numberOfColumnsInMineField + 2; column++)
            {
                buttons[row][column] = new Block(this, row, column);
                buttons[row][column].setDefaults();

                final int currentRow = row;
                final int currentColumn = column;

                buttons[row][column].setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        if(toggleButton.isChecked() && remainBlock != 0){
                            if (!buttons[currentRow][currentColumn].isCovered() && (buttons[currentRow][currentColumn].getNeighborMines() > 0) && !isGameOver)
                            {
                                int nearbyFlaggedBlocks = 0;
                                for (int previousRow = -1; previousRow < 2; previousRow++)
                                {
                                    for (int previousColumn = -1; previousColumn < 2; previousColumn++)
                                    {
                                        if (buttons[currentRow + previousRow][currentColumn + previousColumn].isFlagged())
                                        {
                                            nearbyFlaggedBlocks++;
                                        }
                                    }
                                }
                                if (nearbyFlaggedBlocks == buttons[currentRow][currentColumn].getNeighborMines())
                                {
                                    for (int previousRow = -1; previousRow < 2; previousRow++)
                                    {
                                        for (int previousColumn = -1; previousColumn < 2; previousColumn++)
                                        {
                                            if (!buttons[currentRow + previousRow][currentColumn + previousColumn].isFlagged())
                                            {
                                                rippleUncover(currentRow + previousRow, currentColumn + previousColumn);

                                                if (buttons[currentRow + previousRow][currentColumn + previousColumn].hasMine())
                                                {
                                                    view.setEnabled(false);
                                                    finishGame(currentRow + previousRow, currentColumn + previousColumn);
                                                }
                                                if (checkGameWin())
                                                {
                                                    winGame();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (buttons[currentRow][currentColumn].isClickable() &&
                                    (buttons[currentRow][currentColumn].isEnabled() || buttons[currentRow][currentColumn].isFlagged()))
                            {
                                if (!buttons[currentRow][currentColumn].isFlagged())
                                {
                                    Toast.makeText(getApplicationContext(),"Set Flag (+)",Toast.LENGTH_SHORT).show();
                                    buttons[currentRow][currentColumn].setBlockAsDisabled(false);
                                    buttons[currentRow][currentColumn].setFlagIcon(true);
                                    buttons[currentRow][currentColumn].setFlagged(true);
                                    remainBlock--;
                                    updateMineCountDisplay();
                                }
                                else
                                {
                                    buttons[currentRow][currentColumn].setBlockAsDisabled(true);
                                    buttons[currentRow][currentColumn].clearAllIcons();
                                    if (buttons[currentRow][currentColumn].isFlagged())
                                    {
                                        remainBlock++;
                                        updateMineCountDisplay();
                                    }
                                    buttons[currentRow][currentColumn].setFlagged(false);
                                }
                                updateMineCountDisplay();
                            }
                        }else if(!(toggleButton.isChecked())){
                            if (!areMinesSet)
                            {
                                areMinesSet = true;
                                setMines(currentRow, currentColumn);
                            }
                            if (!buttons[currentRow][currentColumn].isFlagged())
                            {
                                rippleUncover(currentRow, currentColumn);
                                if (buttons[currentRow][currentColumn].hasMine())
                                {
                                    finishGame(currentRow,currentColumn);
                                }
                                if (checkGameWin())
                                {
                                    winGame();
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    private boolean checkGameWin()
    {
        for (int row = 1; row < numberOfRowsInMineField + 1; row++)
        {
            for (int column = 1; column < numberOfColumnsInMineField + 1; column++)
            {
                if (!buttons[row][column].hasMine() && buttons[row][column].isCovered())
                {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateMineCountDisplay()
    {
        if (remainBlock <= 0)
        {
            txtMineCount.setText(Integer.toString(remainBlock));
        }
        else if (remainBlock < 10)
        {
            txtMineCount.setText("0" + Integer.toString(remainBlock));
        }
        else if (remainBlock < 100)
        {
            txtMineCount.setText("0" + Integer.toString(remainBlock));
        }
        else
        {
            txtMineCount.setText(Integer.toString(remainBlock));
        }
    }

    private void winGame()
    {

        isGameOver = true;
        remainBlock = 10;

        updateMineCountDisplay();
        for (int row = 1; row < numberOfRowsInMineField + 1; row++)
        {
            for (int column = 1; column < numberOfColumnsInMineField + 1; column++)
            {
                buttons[row][column].setClickable(false);
                if (buttons[row][column].hasMine())
                {
                    buttons[row][column].setBlockAsDisabled(false);
                    buttons[row][column].setFlagIcon(true);
                }
            }
        }
        Toast.makeText(getApplicationContext(),"                     W I N !                    ",Toast.LENGTH_SHORT).show();
    }

    private void finishGame(int currentRow, int currentColumn)
    {
        isGameOver = true;

        for (int row = 1; row < numberOfRowsInMineField + 1; row++)
        {
            for (int column = 1; column < numberOfColumnsInMineField + 1; column++)
            {
                  buttons[row][column].setBlockAsDisabled(false);
                if (buttons[row][column].hasMine() && !buttons[row][column].isFlagged())
                {
                    buttons[row][column].setMineIcon(false);
                }
                if (!buttons[row][column].hasMine() && buttons[row][column].isFlagged())
                {
                    buttons[row][column].setFlagIcon(false);
                }
                if (buttons[row][column].isFlagged())
                {
                    buttons[row][column].setClickable(false);
                }
            }
        }
        buttons[currentRow][currentColumn].triggerMine();

        Toast.makeText(getApplicationContext(),"                      GAME OVER !                    ",Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),"                  Mines 클릭 시 재시작                 ",Toast.LENGTH_SHORT).show();
    }


    private void setMines(int currentRow, int currentColumn) {
        Random rand = new Random();
        int mineRow, mineColumn;

        for (int row = 0; row < totalNumberOfMines; row++)
        {
            mineRow = rand.nextInt(numberOfColumnsInMineField);
            mineColumn = rand.nextInt(numberOfRowsInMineField);
            if ((mineRow + 1 != currentColumn) || (mineColumn + 1 != currentRow))
            {
                if (buttons[mineColumn + 1][mineRow + 1].hasMine())
                {
                    row--;
                }
                buttons[mineColumn + 1][mineRow + 1].plantMine();
            }
            else
            {
                row--;
            }
        }

        int nearByMineCount;
        for (int row = 0; row < numberOfRowsInMineField + 2; row++)
        {
            for (int column = 0; column < numberOfColumnsInMineField + 2; column++)
            {
                nearByMineCount = 0;
                if ((row != 0) && (row != (numberOfRowsInMineField + 1)) && (column != 0) && (column != (numberOfColumnsInMineField + 1)))
                {
                    for (int previousRow = -1; previousRow < 2; previousRow++)
                    {
                        for (int previousColumn = -1; previousColumn < 2; previousColumn++)
                        {
                            if (buttons[row + previousRow][column + previousColumn].hasMine())
                            {
                                nearByMineCount++;
                            }
                        }
                    }
                    buttons[row][column].setNeighborMines(nearByMineCount);
                }
                else
                {
                    buttons[row][column].setNeighborMines(9);
                    buttons[row][column].OpenBlock();
                }
            }
        }
    }

    private void rippleUncover(int rowClicked, int columnClicked)
    {
        if (buttons[rowClicked][columnClicked].hasMine() || buttons[rowClicked][columnClicked].isFlagged())
        {
            return;
        }
        buttons[rowClicked][columnClicked].OpenBlock();
        if (buttons[rowClicked][columnClicked].getNeighborMines() != 0 )
        {
            return;
        }
        for (int row = 0; row < 3; row++)
        {
            for (int column = 0; column < 3; column++)
            {
                if (buttons[rowClicked + row - 1][columnClicked + column - 1].isCovered()
                        && (rowClicked + row - 1 > 0) && (columnClicked + column - 1 > 0)
                        && (rowClicked + row - 1 < numberOfRowsInMineField + 1) && (columnClicked + column - 1 < numberOfColumnsInMineField + 1))
                {
                    rippleUncover(rowClicked + row - 1, columnClicked + column - 1 );
                }
            }
        }
        return;
    }

}