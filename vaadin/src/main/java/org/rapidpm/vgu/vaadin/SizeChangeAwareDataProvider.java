package org.rapidpm.vgu.vaadin;

import java.util.ArrayList;
import java.util.List;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

public abstract class SizeChangeAwareDataProvider<T, F> extends AbstractBackEndDataProvider<T, F> {

  int lastSize = 0;

  private final List<SizeChangeEventListener> sizeChangeEventListeners = new ArrayList<>();

  @Override
  public int size(Query<T, F> query) {
    int size = super.size(query);

    if (lastSize != size) {
      onSizeChange(size);
    }
    lastSize = size;
    return size;
  }

  private void onSizeChange(int size) {
    for (SizeChangeEventListener sizeChangeEventListener : sizeChangeEventListeners) {
      sizeChangeEventListener.onSizeChange(size);
    }
  }

  public void addSizeChangeListener(SizeChangeEventListener listener) {
    sizeChangeEventListeners.add(listener);
  }
}
