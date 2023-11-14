package io.github.chronosx88.JGUN.models.acks;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Jacksonized
public class PutAck extends BaseAck {}
