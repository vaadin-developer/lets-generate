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
import de.generator.beans.Address;
import de.generator.beans.Contract;
import de.generator.beans.filter.ContractFilter;
import de.generator.beans.filter.ContractSortFields;
import de.generator.beans.repo.ContractBaseQueries;

public class ContractQueries implements ContractBaseQueries, HasLogger {
  private Collection<Contract> contracts;

  public ContractQueries() {
    Set<Contract> contracts = new HashSet<>();
    contracts.add(new Contract(1, "First Contract", null));
    contracts.add(new Contract(2, "2nd. Contract", new Address(1, "Hans", "Wurst", 20, "1234")));
    this.contracts = Collections.unmodifiableCollection(contracts);
  }

  @Override
  public Slice<Contract, ContractSortFields> find(ContractFilter filter,
      OffsetRequest<ContractSortFields> offsetRequest) {
    logger().info("find({}, {})", filter, offsetRequest);
    Stream<Contract> stream = filter(contracts.stream(), filter);
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
    return new SliceImpl<>(stream.collect(Collectors.toList()), offsetRequest);
  }

  private Stream<Contract> filter(Stream<Contract> stream, ContractFilter filter) {
    if (StringUtils.isNotBlank(filter.getName())) {
      stream = stream
          .filter(contract -> StringUtils.containsIgnoreCase(contract.getName(), filter.getName()));
    }
    return stream;
  }



  @Override
  public long count(ContractFilter filter) {
    logger().info("count({})", filter);
    return filter(contracts.stream(), filter).count();
  }

}
