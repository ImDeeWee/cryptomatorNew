package org.cryptomator.ui.mainwindow;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.cryptomator.common.vaults.Vault;
import org.cryptomator.common.vaults.VaultState;
import org.cryptomator.integrations.mount.Mountpoint;
import org.cryptomator.ui.common.FxController;
import org.cryptomator.ui.common.VaultService;
import org.cryptomator.ui.fxapp.FxApplicationWindows;
import org.cryptomator.ui.stats.VaultStatisticsComponent;

import javax.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import java.net.URI;
import java.util.Optional;

@MainWindowScoped
public class VaultDetailUnlockedController implements FxController {

	private final ReadOnlyObjectProperty<Vault> vault;
	private final FxApplicationWindows appWindows;
	private final VaultService vaultService;
	private final Stage mainWindow;
	private final LoadingCache<Vault, VaultStatisticsComponent> vaultStats;
	private final VaultStatisticsComponent.Builder vaultStatsBuilder;
	private final ObservableValue<Mountpoint> mountPoint;
	private final ObservableValue<Boolean> accessibleViaPath;
	private final ObservableValue<Boolean> accessibleViaUri;
	private final ObservableValue<String> mountUri;

	@Inject
	public VaultDetailUnlockedController(ObjectProperty<Vault> vault, FxApplicationWindows appWindows, VaultService vaultService, VaultStatisticsComponent.Builder vaultStatsBuilder, @MainWindow Stage mainWindow) {
		this.vault = vault;
		this.appWindows = appWindows;
		this.vaultService = vaultService;
		this.mainWindow = mainWindow;
		this.vaultStats = CacheBuilder.newBuilder().weakValues().build(CacheLoader.from(this::buildVaultStats));
		this.vaultStatsBuilder = vaultStatsBuilder;
		this.mountPoint = vault.flatMap(Vault::mountPointProperty);
		this.accessibleViaPath = mountPoint.map(m -> m instanceof Mountpoint.WithPath).orElse(false);
		this.accessibleViaUri = mountPoint.map(m -> m instanceof Mountpoint.WithUri).orElse(false);
		this.mountUri = mountPoint.map(Mountpoint::uri).map(URI::toASCIIString).orElse("");
	}

	private VaultStatisticsComponent buildVaultStats(Vault vault) {
		return vaultStatsBuilder.vault(vault).build();
	}

	@FXML
	public void revealAccessLocation() {
		vaultService.reveal(vault.get());
	}

	@FXML
	public void copyMountUri() {
		// TODO
	}

	@FXML
	public void lock() {
		appWindows.startLockWorkflow(vault.get(), mainWindow);
	}

	@FXML
	public void showVaultStatistics() {
		vaultStats.getUnchecked(vault.get()).showVaultStatisticsWindow();
	}

	/* Getter/Setter */

	public ReadOnlyObjectProperty<Vault> vaultProperty() {
		return vault;
	}

	public Vault getVault() {
		return vault.get();
	}

	public ObservableValue<Boolean> accessibleViaPathProperty() {
		return accessibleViaPath;
	}

	public boolean isAccessibleViaPath() {
		return accessibleViaPath.getValue();
	}

	public ObservableValue<Boolean> accessibleViaUriProperty() {
		return accessibleViaUri;
	}

	public boolean isAccessibleViaUri() {
		return accessibleViaUri.getValue();
	}

	public ObservableValue<String> mountUriProperty() {
		return mountUri;
	}

	public String getMountUri() {
		return mountUri.getValue();
	}


}
