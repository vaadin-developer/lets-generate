package de.generator.beans.vaadin;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.infinitenature.commons.pagination.OffsetRequest;
import org.infinitenature.commons.pagination.Slice;
import org.infinitenature.commons.pagination.SortOrder;
import org.infinitenature.commons.pagination.impl.SliceImpl;
import org.rapidpm.dependencies.core.logger.HasLogger;
import de.generator.beans.Contract;
import de.generator.beans.filter.ContractFilter;
import de.generator.beans.filter.ContractSortFields;
import de.generator.beans.repo.ContractBaseQueries;

public class ContractQueries implements ContractBaseQueries, HasLogger {
  public static final Collection<Contract> CONTRACTS;

  static {
    Set<Contract> c = new HashSet<>();
    c.add(new Contract(1, "First Contract", null));
    c.add(new Contract(2, "2nd. Contract", AddressQueries.ADDRESSES.get(0)));
    CONTRACTS = Collections.unmodifiableCollection(c);
  }

  @Override
  public Slice<Contract, ContractSortFields> find(ContractFilter filter,
      OffsetRequest<ContractSortFields> offsetRequest) {
    logger().info("find({}, {})", filter, offsetRequest);
    Stream<Contract> stream = filter(CONTRACTS.stream(), filter);
    SortOrder sortOrder = offsetRequest.getSortOrder();
    switch (offsetRequest.getSortField()) {
      case ID:
        stream = stream.sorted((c1, c2) -> sortOrder == SortOrder.ASC ? c1.getId() - c2.getId()
            : c2.getId() - c1.getId());
        break;
      case NAME:
        stream = stream.sorted(
            (c1, c2) -> sortOrder == SortOrder.ASC ? StringUtils.compare(c1.getName(), c2.getName())
                : StringUtils.compare(c2.getName(), c1.getName()));
        break;
      default:
        break;
    }
    return new SliceImpl<>(stream.skip(offsetRequest.getOffset()).limit(offsetRequest.getCount())
        .collect(Collectors.toList()), offsetRequest);
  }

  private Stream<Contract> filter(Stream<Contract> stream, ContractFilter filter) {
    if (StringUtils.isNotBlank(filter.getName())) {
      stream = stream
          .filter(contract -> StringUtils.containsIgnoreCase(contract.getName(), filter.getName()));
    }
    if (filter.getAddress() != null) {
      stream = stream.filter(contract -> filter.getAddress().equals(contract.getAddress()));
    }
    return stream;
  }



  @Override
  public long count(ContractFilter filter) {
    logger().info("count({})", filter);
    return filter(CONTRACTS.stream(), filter).count();
  }

}
