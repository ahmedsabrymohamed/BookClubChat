package com.fromscratch.mine.bookclub;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fromscratch.mine.bookclub.Adapters.BooksListAdapter;
import com.fromscratch.mine.bookclub.Classes.BookClub;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class NewBooksFragment extends Fragment implements BooksListAdapter.SetOncLickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "Uid";
    private static final String CLUBS_DATA_BRANCH = "clubsData";
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    int position;
    DatabaseReference ref;
    private OnSelectedListenerNewBooks mListener;
    private ArrayList<BookClub> newBookClubArrayList;
    private String uid;
    private BooksListAdapter listAdapter;
    private HashMap<String, BookClub> newBookClubMap;


    public NewBooksFragment() {
        // Required empty public constructor
    }


    public static NewBooksFragment newInstance(String param1) {
        NewBooksFragment fragment = new NewBooksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newBookClubMap = new HashMap<>();
        newBookClubArrayList = new ArrayList<>();
        listAdapter = new BooksListAdapter(getActivity(), this);
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_PARAM1);
        }

        ref = database.getReference(CLUBS_DATA_BRANCH);
        if (savedInstanceState != null) {
            ArrayList<BookClub> clubs = savedInstanceState.getParcelableArrayList("newSelectedClubs");
            for (BookClub club : clubs)
                newBookClubMap.put(club.getClubId(), club);

        }

        getData();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_new_books, container, false);

        manager = new LinearLayoutManager(getActivity());
        recyclerView = root.findViewById(R.id.newBooks_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(listAdapter);
        if (savedInstanceState != null)
            position = savedInstanceState.getInt("listPosition");
        else
            position = 0;


        return root;
    }


    @Override
    public void onStart() {

        super.onStart();


    }


    @Override
    public void SetOnclick(BookClub club) {

    }

    @Override
    public void OnSelectChange(HashMap<String, BookClub> bookClubsSelected) {

        newBookClubMap = bookClubsSelected;
        mListener.onSelectedChangeNewBooks(bookClubsSelected);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSelectedListenerNewBooks) {
            mListener = (OnSelectedListenerNewBooks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("newSelectedClubs", new ArrayList<>(newBookClubMap.values()));
        outState.putInt("listPosition",
                manager.findFirstCompletelyVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }

    private void getData() {
        ref.orderByChild("Users/" + uid).equalTo(null).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                newBookClubArrayList.clear();
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    BookClub bookClub = postSnapshot.getValue(BookClub.class);
                    bookClub.setClubId(postSnapshot.getKey());
                    newBookClubArrayList.add(bookClub);


                }
                listAdapter.refresh();
                listAdapter.setBookClubs(newBookClubMap, newBookClubArrayList);
                recyclerView.scrollToPosition(position);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
    }

    public interface OnSelectedListenerNewBooks {
        void onSelectedChangeNewBooks(HashMap<String, BookClub> bookClubsSelected);
    }
}
