package com.fromscratch.mine.bookclub.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fromscratch.mine.bookclub.Classes.BookClub;
import com.fromscratch.mine.bookclub.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class BooksListAdapter extends RecyclerView.Adapter<BooksListAdapter.BookClubViewHolder> {



    private ArrayList<BookClub> bookClubs;
    private HashMap<String,BookClub> bookClubsSelected;
    private SetOncLickListener listener;
    private Context context;
    public BooksListAdapter(Context context, SetOncLickListener listener) {
        this.listener = listener;
        this.context=context;
        this.bookClubs=new ArrayList<>();
        bookClubsSelected=new HashMap<>();
    }


    @Override
    public BookClubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(  R.layout.bookclub_item, parent, false);
        return new BookClubViewHolder(root);



    }

    @Override
    public void onBindViewHolder(final BookClubViewHolder holder, final int position) {


        holder.bookType.setText(bookClubs.get(position).getBookType());
        holder.bookName.setText(bookClubs.get(position).getBookName());

        if(bookClubs.get(position).isSelected())
            holder.checkbook.setImageDrawable(context.getDrawable(R.mipmap.books));
        else
            holder.checkbook.setImageDrawable(context.getDrawable(R.mipmap.books_unse));

        holder.checkbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookClub bookClub=
                        bookClubs.get(position);

                if(!bookClub.isSelected()) {
                    bookClub.setSelected(true);
                    holder.checkbook.setImageDrawable(context.getDrawable(R.mipmap.books));
                    bookClubsSelected.put(bookClub.getClubId(),bookClub);
                    listener.OnSelectChange( bookClubsSelected);
                }else{
                    bookClub.setSelected(false);
                    holder.checkbook.setImageDrawable(context.getDrawable(R.mipmap.books_unse));
                    bookClubsSelected.remove(bookClub.getClubId());
                    listener.OnSelectChange( bookClubsSelected);
                }

            }
        });



    }

    @Override
    public int getItemCount() {
        return bookClubs.size();
    }

    class BookClubViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        final TextView bookName;
        final TextView bookType;
        final ImageView checkbook;
        public BookClubViewHolder(View itemView) {

            super(itemView);
            bookName =itemView.findViewById(R.id.book_name);
            bookType =itemView.findViewById(R.id.book_type);
            checkbook=itemView.findViewById(R.id.select_image);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            listener.SetOnclick(bookClubs.get(getAdapterPosition()));


        }
    }

    public interface SetOncLickListener{
        void SetOnclick(BookClub club);
        void OnSelectChange(HashMap<String,BookClub> bookClubsSelected);
    }


    public void setBookClubs(HashMap<String, BookClub> bookClubsSelected
            ,ArrayList<BookClub>bookClubArrayList) {
        refresh();
        this.bookClubsSelected = bookClubsSelected;
        for (BookClub club:bookClubArrayList)
        {
            if(bookClubsSelected.containsKey(club.getClubId()))
                club.setSelected(true);
            else
                club.setSelected(false);
            insertItem(club);
        }

    }
    private void insertItem(BookClub bookClub){

        bookClubs.add(bookClub);
       // Log.d("ahmed123", "insertitem");
        this.notifyItemInserted(bookClubs.size()-1);
    }
    public void refresh(){

        bookClubs.clear();
        bookClubsSelected.clear();
        this.notifyDataSetChanged();


    }


    public ArrayList<BookClub> getBookClubs(){
        return  bookClubs;
    }
}






