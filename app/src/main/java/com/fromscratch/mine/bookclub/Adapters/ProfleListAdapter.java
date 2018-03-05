package com.fromscratch.mine.bookclub.Adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fromscratch.mine.bookclub.Classes.BookClub;
import com.fromscratch.mine.bookclub.R;

import java.util.ArrayList;
import java.util.List;


public class ProfleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<BookClub> bookClubs;


    public ProfleListAdapter(ArrayList<BookClub> bookClubs) {

        this.bookClubs = bookClubs;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.profile_book__item, parent, false);
        return new BookClubViewHolder(root);


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        BookClubViewHolder viewHolder = (BookClubViewHolder) holder;
        viewHolder.bookType.setText(bookClubs.get(position).getBookType());
        viewHolder.bookName.setText(bookClubs.get(position).getBookName());


    }

    @Override
    public int getItemCount() {
        return bookClubs.size();
    }

    public void insertItem(BookClub bookClub) {

        bookClubs.add(bookClub);
        notifyItemInserted(bookClubs.size() - 1);
    }

    class BookClubViewHolder extends RecyclerView.ViewHolder {

        final TextView bookName;
        final TextView bookType;

        public BookClubViewHolder(View itemView) {

            super(itemView);
            bookName = itemView.findViewById(R.id.book_name);
            bookType = itemView.findViewById(R.id.book_type);

        }


    }


}



