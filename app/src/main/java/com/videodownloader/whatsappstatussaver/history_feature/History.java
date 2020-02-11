package com.videodownloader.whatsappstatussaver.history_feature;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.videodownloader.whatsappstatussaver.MainActivity;
import com.videodownloader.whatsappstatussaver.R;
import com.videodownloader.whatsappstatussaver.VDFragment;
import com.videodownloader.whatsappstatussaver.utils.Utils;

import java.util.List;

public class History extends VDFragment implements MainActivity.OnBackPressedListener {
    private View view;
    private EditText searchText;
    private RecyclerView visitedPagesView;

    private List<VisitedPage> visitedPages;
    private HistorySQLite historySQLite;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        if (view == null) {
            getVDActivity().setOnBackPressedListener(this);

            view = inflater.inflate(R.layout.history, container, false);

            searchText = view.findViewById(R.id.historySearchText);
            ImageView searchButton = view.findViewById(R.id.historySearchIcon);
            visitedPagesView = view.findViewById(R.id.rvHistoryList);
            ImageView clearHistory = view.findViewById(R.id.btn_delete_history);

            historySQLite = new HistorySQLite(getActivity());
            visitedPages = historySQLite.getAllVisitedPages();

            visitedPagesView.setAdapter(new VisitedPagesAdapter());
            visitedPagesView.setLayoutManager(new LinearLayoutManager(getActivity()));

            clearHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    historySQLite.clearHistory();
                    visitedPages.clear();
                    visitedPagesView.getAdapter().notifyDataSetChanged();
                    isHistoryEmpty();
                }
            });


            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchGo();
                }
            });

            // history search
            final ImageView history_search_cancel = view.findViewById(R.id.history_search_cancel);
            history_search_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchText.getText().clear();
                    searchGo();
                }
            });

            /*hide/show clear button in search view*/
            TextWatcher searchViewTextWatcher = new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.toString().trim().length()==0){
                        history_search_cancel.setVisibility(View.GONE);
                    } else {
                        history_search_cancel.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void afterTextChanged(Editable s) {}
            };

            searchText.addTextChangedListener(searchViewTextWatcher);
            searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    searchGo();
                    return false;
                }
            });

            isHistoryEmpty();
        }

        return view;
    }

    private void searchGo() {
        if (getActivity().getCurrentFocus() != null) {
            Utils.hideSoftKeyboard(getActivity(), getActivity().getCurrentFocus().getWindowToken());
            visitedPages = historySQLite.getVisitedPagesByKeyword(searchText.getText()
                    .toString());
            visitedPagesView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onBackpressed() {
        getVDActivity().getBrowserManager().unhideCurrentWindow();
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    private class VisitedPagesAdapter extends RecyclerView.Adapter<VisitedPagesAdapter.VisitedPageItem> {
        @Override
        public VisitedPagesAdapter.VisitedPageItem onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VisitedPagesAdapter.VisitedPageItem(LayoutInflater.from(getActivity()).inflate(R.layout.history_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VisitedPageItem holder, int position) {
            holder.bind(visitedPages.get(position));
        }

        @Override
        public int getItemCount() {
            return visitedPages.size();
        }

        class VisitedPageItem extends RecyclerView.ViewHolder {
            private TextView title;
            private TextView subtitle;

            VisitedPageItem(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.row_history_title);
                subtitle = itemView.findViewById(R.id.row_history_subtitle);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getVDActivity().browserClicked();
                        getVDActivity().getBrowserManager().newWindow(visitedPages.get(getAdapterPosition()).link);
                    }
                });
                itemView.findViewById(R.id.row_history_menu).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final PopupMenu popup = new PopupMenu(getActivity(), view);
                        popup.getMenuInflater().inflate(R.menu.history_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                int i = item.getItemId();
                                if (i == R.id.history_delete) {
                                    historySQLite.deleteFromHistory(visitedPages.get(getAdapterPosition()).link);
                                    visitedPages.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
                                    isHistoryEmpty();
                                    return true;
                                }
                                else if (i == R.id.history_open){
                                    getVDActivity().browserClicked();
                                    getVDActivity().getBrowserManager().newWindow(visitedPages.get(getAdapterPosition()).link);
                                    return true;
                                }
                                else if (i == R.id.history_copy) {
                                    Toast.makeText(getActivity(),getString(R.string.copy_msg),Toast.LENGTH_SHORT).show();
                                    ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                                    clipboardManager.setPrimaryClip(ClipData.newPlainText("Copied URL",visitedPages.get(getAdapterPosition()).link));
                                    return true;
                                }
                                else {
                                    return onMenuItemClick(item);
                                }
                            }
                        });
                        popup.show();
                    }
                });
            }

            void bind(VisitedPage page) {
                title.setText(page.title);
                subtitle.setText(page.link);
            }
        }
    }

    private void isHistoryEmpty(){
        if(visitedPages.isEmpty()){
            view.findViewById(R.id.llNoHistory).setVisibility(View.VISIBLE);
            view.findViewById(R.id.historySearchBar).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.llShowHistory).setVisibility(View.INVISIBLE);
        }else{
            view.findViewById(R.id.llNoHistory).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.historySearchBar).setVisibility(View.VISIBLE);
            view.findViewById(R.id.llShowHistory).setVisibility(View.VISIBLE);
        }
    }
}
