package de.generator.beans.vaadin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.infinitenature.commons.pagination.OffsetRequest;
import org.infinitenature.commons.pagination.Slice;
import org.infinitenature.commons.pagination.SortOrder;
import org.infinitenature.commons.pagination.impl.SliceImpl;
import org.rapidpm.dependencies.core.logger.HasLogger;
import de.generator.beans.Address;
import de.generator.beans.filter.AddressFilter;
import de.generator.beans.filter.AddressSortFields;
import de.generator.beans.repo.AddressBaseQueries;

public class AddressQueries implements AddressBaseQueries, HasLogger {
  public static final List<Address> ADDRESSES;

  static {
    List<Address> c = new ArrayList<>();
    c.add(new Address(1, "Hans", "Wurst", 20, "1234"));
    ADDRESSES = Collections.unmodifiableList(c);
  }

  @Override
  public Slice<Address, AddressSortFields> find(AddressFilter filter,
      OffsetRequest<AddressSortFields> offsetRequest) {
    Stream<Address> stream = filter(ADDRESSES.stream(), filter);
    SortOrder sortOrder = offsetRequest.getSortOrder();
    switch (offsetRequest.getSortField()) {
      case ID:
        stream = stream.sorted((c1, c2) -> sortOrder == SortOrder.ASC ? c1.getId() - c2.getId()
            : c2.getId() - c1.getId());
        break;
      case FRIST_NAME:
        stream = stream.sorted((c1, c2) -> sortOrder == SortOrder.ASC
            ? StringUtils.compare(c1.getFristName(), c2.getFristName())
            : StringUtils.compare(c2.getFristName(), c1.getFristName()));
        break;
      default:
        break;
    }
    return new SliceImpl<>(stream.skip(offsetRequest.getOffset()).limit(offsetRequest.getCount())
        .collect(Collectors.toList()), offsetRequest);

  }

  private Stream<Address> filter(Stream<Address> stream, AddressFilter filter) {
    if (StringUtils.isNotBlank(filter.getName())) {
      stream = stream.filter(
          contract -> StringUtils.containsIgnoreCase(contract.getFristName(), filter.getName())
              || StringUtils.containsIgnoreCase(contract.getLastName(), filter.getName()));
    }
    return stream;
  }

  @Override
  public long count(AddressFilter filter) {
    logger().info("count({})", filter);
    return filter(ADDRESSES.stream(), filter).count();
  }

}
