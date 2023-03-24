import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {ToastrModule} from 'ngx-toastr';

import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {AutocompleteComponent} from './component/autocomplete/autocomplete.component';
import {HeaderComponent} from './component/header/header.component';
import {HorseCreateEditComponent} from './component/horse/horse-create-edit/horse-create-edit.component';
import {HorseComponent} from './component/horse/horse.component';
import { InfoComponent } from './component/horse/info/info.component';
import { OwnerComponent } from './component/owner/owner.component';
import { OwnerCreateComponent } from './component/owner/owner-create/owner-create.component';
import { FamilyTreeComponent } from './component/horse/family-tree/family-tree.component';
import { HorseFamilyTreeNodeComponent } from './component/horse/family-tree/horse-family-tree-node/horse-family-tree-node.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    HorseComponent,
    HorseCreateEditComponent,
    AutocompleteComponent,
    InfoComponent,
    OwnerComponent,
    OwnerCreateComponent,
    FamilyTreeComponent,
    HorseFamilyTreeNodeComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ToastrModule.forRoot(),
    // Needed for Toastr
    BrowserAnimationsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
