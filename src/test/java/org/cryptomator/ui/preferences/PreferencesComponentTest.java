package org.cryptomator.ui.preferences;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cryptomator.ui.preferences.PreferencesComponent;
import org.cryptomator.ui.preferences.SelectedPreferencesTab;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import dagger.Lazy;

import static org.mockito.Mockito.*;

class PreferencesComponentTest {

    private PreferencesComponent preferencesComponent;
    private Stage mockStage;
    private Lazy<Scene> mockScene;
    private ObjectProperty<SelectedPreferencesTab> selectedTabProperty;

    @BeforeEach
    void setUp() {
        // Mocker les dépendances
        mockStage = mock(Stage.class);
        mockScene = mock(Lazy.class);
        selectedTabProperty = new SimpleObjectProperty<>();

        // Créer une instance de la classe sous test en mockant l'interface
        preferencesComponent = mock(PreferencesComponent.class, Mockito.CALLS_REAL_METHODS);

        // Simuler les méthodes de l'interface
        when(preferencesComponent.window()).thenReturn(mockStage);
        when(preferencesComponent.scene()).thenReturn(mockScene);
        when(mockScene.get()).thenReturn(mock(Scene.class));
        when(preferencesComponent.selectedTabProperty()).thenReturn(selectedTabProperty);
    }

    @Test
    void testShowPreferencesWindow() {

        //Randomized the selectedPreferencesTab value for each test.
        SelectedPreferencesTab[] tab = SelectedPreferencesTab.values();
        int min = 0;
        int max = tab.length-1;
        int randomIndex = (int)(Math.random() * (max - min + 1)) + min;




        SelectedPreferencesTab testTab = tab[randomIndex];

        // Appeler la méthode sous test
        Stage stage = preferencesComponent.showPreferencesWindow(testTab);

        // Vérifier que l'onglet a été mis à jour correctement
        Assertions.assertEquals(testTab, selectedTabProperty.get());

        // Vérifier que la scène et la fenêtre sont configurées correctement
        verify(mockStage).setScene(mockScene.get());
        verify(mockStage).setMinWidth(420);
        verify(mockStage).setMinHeight(300);
        verify(mockStage).show();
        verify(mockStage).requestFocus();
    }
}
