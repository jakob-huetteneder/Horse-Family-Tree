import { Component } from '@angular/core';
import {NgForm, NgModel} from '@angular/forms';
import {Observable} from 'rxjs';
import {Owner} from '../../../dto/owner';
import {HorseService} from '../../../service/horse.service';
import {OwnerService} from '../../../service/owner.service';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-owner-create',
  templateUrl: './owner-create.component.html',
  styleUrls: ['./owner-create.component.scss']
})
export class OwnerCreateComponent {
  owner: Owner = {
    firstName: '',
    lastName: '',
    email: '',
  };

  constructor(
    private service: OwnerService,
    private horseService: HorseService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }


  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.owner);
    if (form.valid) {
      if (this.owner.email === '') {
        delete this.owner.email;
      }
      const observable: Observable<Owner> = this.service.create(this.owner);

      observable.subscribe({
        next: () => {
          this.notification.success(`Owner ${this.owner.firstName} ${this.owner.lastName} successfully created.`);
          this.router.navigate(['/owners']);
        },
        error: error => {
          console.error('Error creating owner', error);
          this.notification.error('Error creating owner', error.error.message);
        }
      });
    }
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      // This names in this object are determined by the style library,
      // requiring it to follow TypeScript naming conventions does not make sense.
      // eslint-disable-next-line @typescript-eslint/naming-convention
      'is-invalid': !input.valid && !input.pristine,
    };
  }

}
