// Generated by view binder compiler. Do not edit!
package com.example.sistemjemuran.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.sistemjemuran.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityHistoryBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final ImageButton backButton;

  @NonNull
  public final FloatingActionButton deleteButton;

  @NonNull
  public final LinearLayout headerTitle;

  @NonNull
  public final TextView historyList;

  @NonNull
  public final RecyclerView listHistory;

  @NonNull
  public final TextView noDataText;

  private ActivityHistoryBinding(@NonNull RelativeLayout rootView, @NonNull ImageButton backButton,
      @NonNull FloatingActionButton deleteButton, @NonNull LinearLayout headerTitle,
      @NonNull TextView historyList, @NonNull RecyclerView listHistory,
      @NonNull TextView noDataText) {
    this.rootView = rootView;
    this.backButton = backButton;
    this.deleteButton = deleteButton;
    this.headerTitle = headerTitle;
    this.historyList = historyList;
    this.listHistory = listHistory;
    this.noDataText = noDataText;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityHistoryBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityHistoryBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_history, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityHistoryBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.backButton;
      ImageButton backButton = ViewBindings.findChildViewById(rootView, id);
      if (backButton == null) {
        break missingId;
      }

      id = R.id.deleteButton;
      FloatingActionButton deleteButton = ViewBindings.findChildViewById(rootView, id);
      if (deleteButton == null) {
        break missingId;
      }

      id = R.id.header_title;
      LinearLayout headerTitle = ViewBindings.findChildViewById(rootView, id);
      if (headerTitle == null) {
        break missingId;
      }

      id = R.id.historyList;
      TextView historyList = ViewBindings.findChildViewById(rootView, id);
      if (historyList == null) {
        break missingId;
      }

      id = R.id.listHistory;
      RecyclerView listHistory = ViewBindings.findChildViewById(rootView, id);
      if (listHistory == null) {
        break missingId;
      }

      id = R.id.noDataText;
      TextView noDataText = ViewBindings.findChildViewById(rootView, id);
      if (noDataText == null) {
        break missingId;
      }

      return new ActivityHistoryBinding((RelativeLayout) rootView, backButton, deleteButton,
          headerTitle, historyList, listHistory, noDataText);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
