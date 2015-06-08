package org.gradoop.model.impl;

import org.gradoop.model.Edge;

import java.util.Map;

/**
 * Transient representation of an edge.
 */
public class DefaultEdge extends LabeledPropertyContainer implements Edge {
  /**
   * Identifier of the vertex this edge is connected to. This can be either
   * the start or end vertex of this edge.
   */
  private final Long otherID;

  /**
   * Creates an edge instance based on the given parameters.
   *
   * @param otherID    the id of the vertex that edge is connected to
   * @param label      edge label
   * @param index      vertex centric edge index for parallel edges
   * @param properties edge properties
   */
  DefaultEdge(final Long otherID, final String label, final Long index,
    final Map<String, Object> properties) {
    super(index, label, properties);
    this.otherID = otherID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getOtherID() {
    return this.otherID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getIndex() {
    return this.id;
  }

  /**
   * Equality of edges is only valid in the context of a single vertex. Two
   * edges are equal if they have the same otherID, label and index.
   *
   * @param o edge to check equality to
   * @return true if the edge is equal to the given object, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DefaultEdge that = (DefaultEdge) o;

    return id.equals(that.id) && otherID.equals(that.otherID) &&
      getLabel().equals(that.getLabel());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = otherID.hashCode();
    result = 31 * result + id.hashCode();
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "DefaultEdge{" +
      "otherID=" + otherID +
      ", label=" + getLabel() +
      ", index=" + id +
      '}';
  }
}
