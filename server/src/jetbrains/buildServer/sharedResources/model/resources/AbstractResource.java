package jetbrains.buildServer.sharedResources.model.resources;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public abstract class AbstractResource implements Resource {

  @NotNull
  private final String myName;

  @NotNull
  private final ResourceType myType;

  private final boolean myState;

  protected AbstractResource(@NotNull final String name, @NotNull final ResourceType type, boolean state) {
    myName = name;
    myType = type;
    myState = state;
  }

  @NotNull
  @Override
  public final String getName() {
    return myName;
  }

  @NotNull
  @Override
  public final ResourceType getType() {
    return myType;
  }

  @Override
  public final boolean isEnabled() {
    return myState;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AbstractResource)) return false;
    AbstractResource that = (AbstractResource) o;
    return myName.equals(that.myName);
  }

  @Override
  public int hashCode() {
    return myName.hashCode();
  }
}
